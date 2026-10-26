#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT_DIR="$ROOT_DIR/target/gc-compare"
WORKLOAD_CLASS="com.tutorial.memory.demos.GcComparisonWorkload"
DURATION_SECONDS="${1:-20}"

mkdir -p "$OUT_DIR"

cd "$ROOT_DIR"
mvn -q -DskipTests compile

echo "Running GC comparison workload for ${DURATION_SECONDS}s with G1 and ZGC"

run_gc() {
  local gc_name="$1"
  local gc_flag="$2"
  local log_file="$OUT_DIR/${gc_name}.log"
  local time_file="$OUT_DIR/${gc_name}.time"

  echo "Starting run: ${gc_name}"
  /usr/bin/time -p -o "$time_file" \
    java \
      -Xms512m -Xmx512m \
      "$gc_flag" \
      -Xlog:gc*:file="$log_file":time,uptime,level,tags \
      -cp target/classes \
      "$WORKLOAD_CLASS" "$DURATION_SECONDS" >/dev/null

  echo "Finished run: ${gc_name}"
}

run_gc "g1" "-XX:+UseG1GC"
run_gc "zgc" "-XX:+UseZGC"

extract_time() {
  local file="$1"
  awk '$1=="real" {print $2}' "$file"
}

g1_real="$(extract_time "$OUT_DIR/g1.time")"
zgc_real="$(extract_time "$OUT_DIR/zgc.time")"

g1_gc_events="$(grep -c " GC(" "$OUT_DIR/g1.log" || true)"
zgc_gc_events="$(grep -c " GC(" "$OUT_DIR/zgc.log" || true)"

cat >"$OUT_DIR/summary.txt" <<EOF
GC Comparison Summary
=====================
Duration per run: ${DURATION_SECONDS}s

G1
- Wall time (s): ${g1_real}
- GC log events: ${g1_gc_events}
- Log file: $OUT_DIR/g1.log

ZGC
- Wall time (s): ${zgc_real}
- GC log events: ${zgc_gc_events}
- Log file: $OUT_DIR/zgc.log
EOF

echo
cat "$OUT_DIR/summary.txt"
echo
echo "Done. Detailed logs and timing files are in: $OUT_DIR"
