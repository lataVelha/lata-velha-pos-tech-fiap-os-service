# Infraestrutura do os-service

Este diretório era uma cópia da documentação de infraestrutura do monolito
(`app`), que foi removido junto com o repo `infra-db`.

A infraestrutura real do os-service está em:

- `terraform/` — ECR próprio, **RDS próprio** (database `lata_velha_os`) e
  Deployment/Service/ConfigMap/Secret/HPA/PDB no namespace `lata-velha-os`.
- `k8s/` — manifests renderizados pelo Terraform.
- `.github/workflows/main.yml` — pipeline de CI/CD do serviço.

NodePort: **30082**. State: `lata-velha/os-service/terraform.tfstate`.
