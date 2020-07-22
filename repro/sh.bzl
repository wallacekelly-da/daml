def _sh_inline_script_impl(ctx):
    cmd = ctx.attr.cmd
    cmd = ctx.expand_location(cmd, ctx.attr.data)
    cmd = ctx.expand_make_variables("cmd", cmd, {})
    ctx.actions.expand_template(
        template = ctx.file._template,
        output = ctx.outputs.output,
        is_executable = True,
        substitutions = {
            "%cmd%": cmd,
        },
    )

    runfiles = ctx.runfiles(files = [ctx.outputs.output] + ctx.files.data)
    for data_dep in ctx.attr.data:
        runfiles = runfiles.merge(data_dep[DefaultInfo].default_runfiles)

    return DefaultInfo(
        files = depset([ctx.outputs.output]),
        runfiles = runfiles,
    )

_sh_inline_script = rule(
    _sh_inline_script_impl,
    attrs = {
        "cmd": attr.string(
            mandatory = True,
        ),
        "data": attr.label_list(
            allow_files = True,
        ),
        "output": attr.output(
            mandatory = True,
        ),
        "_template": attr.label(
            allow_single_file = True,
            default = "//repro:sh.tpl",
        ),
    },
)

def sh_inline_test(
        name,
        cmd,
        data = [],
        **kwargs):
    testonly = kwargs.pop("testonly", True)
    _sh_inline_script(
        name = name + "_script",
        cmd = cmd,
        output = name + ".sh",
        data = data,
        testonly = testonly,
    )
    native.sh_test(
        name = name,
        data = data,
        deps = ["@bazel_tools//tools/bash/runfiles"],
        srcs = [name + ".sh"],
        testonly = testonly,
        **kwargs
    )
