terraform {
  backend "s3" {
    key = "lata-velha/os-service/terraform.tfstate"
    # Lock de estado nativo do S3 (sem DynamoDB)
    use_lockfile = true
  }
}
