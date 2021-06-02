#!/usr/bin/env bash
set -euo pipefail

wi=2
i=5

bench() {
bazel run //daml-lf/scenario-interpreter:scenario-perf -- -f 1 -i $i -wi $wi  2>&1 | grep 'Average]'
}

run() {
mode=$1
git checkout daml-lf/interpreter/src/main/scala/com/digitalasset/daml/lf/transaction/PartialTransaction.scala
sed -i "s/XXX/$mode/" daml-lf/interpreter/src/main/scala/com/digitalasset/daml/lf/transaction/PartialTransaction.scala
bench | xargs echo "$mode: "
bench | xargs echo "$mode: "
bench | xargs echo "$mode: "
}

run Off
run On
run Off
run On
