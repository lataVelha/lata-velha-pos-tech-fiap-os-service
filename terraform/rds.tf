# Banco proprio do os-service (database-per-service).
#
# Mesmo padrao do billing-service: o RDS nao e mais compartilhado com o
# `app`/`infra-db`. Cada microsservico Postgres tem sua propria instancia,
# com subnet group, security group, usuario e senha proprios.
#
# VPC/subnets sao descobertas direto no EKS (como o billing faz), sem
# terraform_remote_state do bootstrap.

data "aws_eks_cluster" "this" {
  name = var.cluster_name
}

locals {
  name               = "${var.project_name}-os"
  vpc_id             = data.aws_eks_cluster.this.vpc_config[0].vpc_id
  private_subnet_ids = tolist(data.aws_eks_cluster.this.vpc_config[0].subnet_ids)
}

data "aws_vpc" "this" {
  id = local.vpc_id
}

resource "random_password" "db" {
  length  = 24
  special = false
}

resource "aws_db_subnet_group" "os" {
  name       = "${local.name}-subnet-group"
  subnet_ids = local.private_subnet_ids

  tags = {
    Name = "${local.name}-subnet-group"
  }
}

resource "aws_security_group" "rds" {
  name        = "${local.name}-rds-sg"
  description = "Acesso ao RDS do os-service a partir da VPC"
  vpc_id      = local.vpc_id

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = [data.aws_vpc.this.cidr_block]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "${local.name}-rds-sg"
  }
}

resource "aws_db_instance" "os" {
  identifier              = "${local.name}-postgres"
  engine                  = "postgres"
  engine_version          = "15"
  instance_class          = var.rds_instance_class
  allocated_storage       = 20
  db_name                 = var.db_name
  username                = var.db_username
  password                = random_password.db.result
  db_subnet_group_name    = aws_db_subnet_group.os.name
  vpc_security_group_ids  = [aws_security_group.rds.id]
  skip_final_snapshot     = true
  backup_retention_period = 0
  publicly_accessible     = false
  multi_az                = false
  storage_encrypted       = true

  tags = {
    Name = "${local.name}-postgres"
  }
}
