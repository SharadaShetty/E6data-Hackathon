#!/bin/bash

OUTPUT_DIR="results"
SUMMARY_FILE="$OUTPUT_DIR/summary.txt"

mkdir -p "$OUTPUT_DIR"
TIMESTAMP=$(date +"%H:%M:%S")

echo "QUERY ENGINE EXECUTION SUMMARY" > "$SUMMARY_FILE"
echo "=============================" >> "$SUMMARY_FILE"
echo "Timestamp: $TIMESTAMP" >> "$SUMMARY_FILE"
echo "" >> "$SUMMARY_FILE"

run_query() {
    QUERY=$1
    OUTPUT_FILE="$OUTPUT_DIR/${QUERY}.txt"
    ERROR_FILE="$OUTPUT_DIR/${QUERY}_error.log"

    START_TIME=$(date +%s.%N)

    java TaxiQueryEngine "$QUERY" > "$OUTPUT_FILE" 2>"$ERROR_FILE"
    EXIT_CODE=$?

    END_TIME=$(date +%s.%N)
    EXECUTION_TIME=$(echo "$END_TIME - $START_TIME" | bc)



    if [ $EXIT_CODE -eq 0 ] && [ -s "$OUTPUT_FILE" ]; then
        STATUS="SUCCESS"
    else
        STATUS="FAILED"
    fi

    echo "[$QUERY]" >> "$SUMMARY_FILE"
    echo "Status: $STATUS" >> "$SUMMARY_FILE"
    echo "Execution time: ${EXECUTION_TIME}s" >> "$SUMMARY_FILE"

    if [ "$STATUS" == "FAILED" ]; then
        ERROR_LOG=$(cat "$ERROR_FILE")
        echo "Error details: $ERROR_LOG" >> "$SUMMARY_FILE"
    fi

    echo "" >> "$SUMMARY_FILE"

    echo "$QUERY: ${EXECUTION_TIME}s - $STATUS"
}

if [ ! -z "$1" ] && [[ "$1" =~ ^query[1-4]$ ]]; then
    run_query "$1"
    exit 0
elif [ ! -z "$1" ]; then
    echo "Error: Invalid query parameter '$1'. Use query1, query2, query3, or query4."
    exit 1
fi

echo "Running all queries..."

for i in {1..4}; do
    run_query "query$i"
done

SUCCESS_COUNT=$(grep -c "Status: SUCCESS" "$SUMMARY_FILE")
FAILED_COUNT=$(grep -c "Status: FAILED" "$SUMMARY_FILE")

echo "OVERALL STATS:" >> "$SUMMARY_FILE"
echo "-------------" >> "$SUMMARY_FILE"
echo "Total: 4, Success: $SUCCESS_COUNT, Failed: $FAILED_COUNT" >> "$SUMMARY_FILE"

echo "----------------------------------------"
echo "All done. Summary saved to $SUMMARY_FILE"
echo "Total: 4, Success: $SUCCESS_COUNT, Failed: $FAILED_COUNT"