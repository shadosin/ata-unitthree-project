#!/bin/bash
set -eo pipefail

echo "Deleting Application UNIT_THREE_DEPLOY_STACK"
echo "This may take 2-3 minutes...  But if takes more than 5 minutes then it may have failed. Check your CloudFormation Stack on the AWS UI for errors."
aws cloudformation delete-stack --stack-name $UNIT_THREE_DEPLOY_STACK
aws cloudformation wait stack-delete-complete --stack-name $UNIT_THREE_DEPLOY_STACK


echo "Checking Artifact Bucket $UNIT_THREE_ARTIFACT_BUCKET"
if [ -z "$(aws s3api head-bucket --bucket "$UNIT_THREE_ARTIFACT_BUCKET" 2>&1)" ] ; then
  echo "Deleting Artifact Bucket $UNIT_THREE_ARTIFACT_BUCKET"
  aws s3 rm s3://$UNIT_THREE_ARTIFACT_BUCKET --recursive
  aws s3api delete-objects --bucket $UNIT_THREE_ARTIFACT_BUCKET --delete "$(aws s3api list-object-versions --bucket $UNIT_THREE_ARTIFACT_BUCKET --query='{Objects: Versions[].{Key:Key,VersionId:VersionId}}')" 1>/dev/null
  aws s3api delete-objects --bucket $UNIT_THREE_ARTIFACT_BUCKET --delete "$(aws s3api list-object-versions --bucket $UNIT_THREE_ARTIFACT_BUCKET --query='{Objects: DeleteMarkers[].{Key:Key,VersionId:VersionId}}')" 1>/dev/null
  aws s3 rb --force s3://$UNIT_THREE_ARTIFACT_BUCKET
fi

echo "Deleting Pipeline $UNIT_THREE_PIPELINE_STACK"
echo "This may take 2-3 minutes...  But if takes more than 5 minutes then it may have failed. Check your CloudFormation Stack on the AWS UI for errors."
aws cloudformation delete-stack --stack-name $UNIT_THREE_PIPELINE_STACK
aws cloudformation wait stack-delete-complete --stack-name $UNIT_THREE_PIPELINE_STACK

rm -rf build .gradle target
