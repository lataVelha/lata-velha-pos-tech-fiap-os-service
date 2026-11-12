output "ecr_repository_url" {
  description = "URL do repositorio ECR exclusivo do os-service"
  value       = aws_ecr_repository.os_service.repository_url
}
