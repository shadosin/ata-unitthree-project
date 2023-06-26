failures=0
trap 'failures=$((failures+1))' ERR
./gradlew expensetracking-integration-task1
./gradlew expensetracking-integration-task2
./gradlew expensetracking-integration-task3
./gradlew expensetracking-integration-task4
./gradlew expensetracking-integration-task5
if ((failures == 0)); then
  echo "Success"
else
  echo "$failures failures"
  exit 1
fi
