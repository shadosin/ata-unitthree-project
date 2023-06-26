#!/bin/bash
set -eo pipefail

source ./setupEnvironment.sh

TABLE_NAME="Expense"
echo "Checking Table $TABLE_NAME"
{
  aws dynamodb delete-table --table-name $TABLE_NAME > /dev/null 2>&1 &&
  echo "Deleting Table $TABLE_NAME" &&
  echo "This may take 2-3 minutes...  But if takes more than 5 minutes then it may have failed. Check your DynamoDB tables on the AWS UI for errors." &&
  ( aws dynamodb wait table-not-exists --table-name $TABLE_NAME ||
   echo "Table may have not deleted. Check your DynamoDB tables on the AWS UI for errors." )
} || {
  echo "Table $TABLE_NAME does not exist"
} ;

TABLE_NAME="ExpenseList"
echo "Checking Table $TABLE_NAME"
{
  aws dynamodb delete-table --table-name $TABLE_NAME > /dev/null 2>&1 &&
  echo "Deleting Table $TABLE_NAME" &&
  echo "This may take 2-3 minutes...  But if takes more than 5 minutes then it may have failed. Check your DynamoDB tables on the AWS UI for errors." &&
  ( aws dynamodb wait table-not-exists --table-name $TABLE_NAME ||
   echo "Table may have not deleted. Check your DynamoDB tables on the AWS UI for errors." )
} || {
  echo "Table $TABLE_NAME does not exist"
} ;

