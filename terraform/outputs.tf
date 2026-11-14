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

output "db_username" {
  description = "Usuario master do RDS do os-service (consumido pela lambda auth-cpf)"
  value       = var.db_username
  sensitive   = true
}

output "db_password" {
  description = "Senha do RDS do os-service (consumida pela lambda auth-cpf)"
  value       = random_password.db.result
  sensitive   = true
}
