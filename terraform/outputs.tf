output "ecr_repository_url" {
  description = "URL do repositorio ECR exclusivo do os-service"
  value       = aws_ecr_repository.os_service.repository_url
}

output "rds_endpoint" {
  description = "Endpoint do RDS exclusivo do os-service"
  value       = aws_db_instance.os.endpoint
}

output "db_name" {
  description = "Nome do banco do os-service"
  value       = aws_db_instance.os.db_name
}
