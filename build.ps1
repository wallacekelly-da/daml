Set-StrictMode -Version latest
$ErrorActionPreference = 'Stop'

# See https://github.com/lukesampson/scoop/issues/3859
Set-Strictmode -Off
.\dev-env\windows\bin\dadew.ps1 install
Set-StrictMode -Version latest
.\dev-env\windows\bin\dadew.ps1 sync
.\dev-env\windows\bin\dadew.ps1 enable

if (!(Test-Path .\.bazelrc.local)) {
   Set-Content -Path .\.bazelrc.local -Value 'build --config windows'
}

$ARTIFACT_DIRS = if ("$env:BUILD_ARTIFACTSTAGINGDIRECTORY") { $env:BUILD_ARTIFACTSTAGINGDIRECTORY } else { Get-Location }

if (!(Test-Path ${ARTIFACT_DIRS}/logs)) {
    mkdir -p ${ARTIFACT_DIRS}/logs
} elseif (Test-Path ${ARTIFACT_DIRS}/logs -PathType Leaf) {
    throw ("Cannot create directory '${ARTIFACT_DIRS}/logs'. Conflicting file.")
}

# If a previous build was forcefully terminated, then stack's lock file might
# not have been cleaned up properly leading to errors of the form
#
#   user error (hTryLock: lock already exists: C:\Users\u\AppData\Roaming\stack\pantry\hackage\hackage-security-lock)
#
# The package cache might be corrupted and just removing the lock might lead to
# errors as below, so we just nuke the entire stack cache.
#
#   Failed populating package index cache
#   IncompletePayload 56726464 844
#
if (Test-Path -Path $env:appdata\stack\pantry\hackage\hackage-security-lock) {
    Write-Output ">> Nuking stack directory"
    Remove-Item -ErrorAction Continue -Force -Recurse -Path $env:appdata\stack
}

function bazel() {
    Write-Output ">> bazel $args"
    $global:lastexitcode = 0
    $backupErrorActionPreference = $script:ErrorActionPreference
    $script:ErrorActionPreference = "Continue"
    & bazel.exe @args 2>&1 | %{ "$_" }
    $script:ErrorActionPreference = $backupErrorActionPreference
    if ($global:lastexitcode -ne 0 -And $args[0] -ne "shutdown") {
        Write-Output "<< bazel $args (failed, exit code: $global:lastexitcode)"
        throw ("Bazel returned non-zero exit code: $global:lastexitcode")
    }
    Write-Output "<< bazel $args (ok)"
}

# ScalaCInvoker, a Bazel worker, created by rules_scala opens some of the bazel execroot's files,
# which later causes issues on Bazel init (source forest creation) on Windows. A shutdown closes workers,
# which is a workaround for this problem.
bazel shutdown

# Prefetch nodejs_dev_env to avoid permission denied errors on external/nodejs_dev_env/nodejs_dev_env/node.exe
# It isn’t clear where exactly those errors are coming from.
bazel fetch @nodejs_dev_env//...

bazel build //... `
  `-`-profile build-profile.json `
  `-`-experimental_profile_include_target_label `
  `-`-build_event_json_file build-events.json `
  `-`-build_event_publish_all_actions `
  `-`-experimental_execution_log_file ${ARTIFACT_DIRS}/logs/build_execution_windows.log

bazel shutdown

if ($env:SKIP_TESTS -ceq "False") {
    # Generate mapping from shortened scala-test names on Windows to long names on Linux and MacOS.
    ./ci/remap-scala-test-short-names.ps1 `
      | Out-File -Encoding UTF8 -NoNewline scala-test-suite-name-map.json

    $skip_dev_canton = "True"
    $tag_filter = ""
    if ($skip_dev_canton -ceq "True") {
        $tag_filter = "-dev-canton-test"
    }

    bazel test //... `
      `-`-build_tag_filters "$tag_filter" `
      `-`-test_tag_filters "$tag_filter" `
      `-`-profile test-profile.json `
      `-`-experimental_profile_include_target_label `
      `-`-build_event_json_file test-events.json `
      `-`-build_event_publish_all_actions `
      `-`-experimental_execution_log_file ${ARTIFACT_DIRS}/logs/test_execution_windows.log
}
