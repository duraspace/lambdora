# Lambdora
A serverless implementation of the Fedora Repository API on AWS. Uses the following technologies:
* [Serverless.com](https://serverless.com/) toolkit
* [API Gateway](https://aws.amazon.com/api-gateway/)
* [Lambda](https://aws.amazon.com/lambda/)
* [DynamoDB](https://aws.amazon.com/dynamodb/)

## Setup
1. Download and install serverless and configure aws credentials
   https://serverless.com/framework/docs/providers/aws/guide/quick-start/#pre-requisites
1. Install Gradle: https://gradle.org/install/
1. ``gradle wrapper``
1. ``./gradlew build``
1. Deploy 
   ``serverless deploy --aws-profile <your-profile-name> [--region <aws-region-name-here>]``
1. Read up on [3 Tips for faster development](https://serverless.com/blog/quick-tips-for-faster-serverless-development/).

## Teardown

If you wish to redeploy updates, you may need to first teardown the existing stack. 
```
serverless remove --aws-profile <your-profile-name> [--region <aws-region-name-here>]
```
