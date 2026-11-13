locals {
  k8s_dir = "${path.module}/../../../k8s"
}

resource "kubectl_manifest" "namespace" {
  yaml_body = file("${local.k8s_dir}/namespace.yaml")
}

resource "kubectl_manifest" "serviceaccount" {
  yaml_body  = file("${local.k8s_dir}/serviceaccount.yaml")
  depends_on = [kubectl_manifest.namespace]
}

resource "kubectl_manifest" "configmap" {
  yaml_body = templatefile("${local.k8s_dir}/configmap.yaml", {
    messaging_topic_arn = var.messaging_topic_arn
    messaging_queue_url = var.messaging_queue_url
    service_version     = var.service_version
    db_schema           = var.db_schema
  })
  depends_on = [kubectl_manifest.namespace]
}

resource "kubectl_manifest" "secret_db" {
  yaml_body = templatefile("${local.k8s_dir}/secret.yaml", {
    db_url_b64        = base64encode(var.db_url)
    db_username_b64   = base64encode(var.db_username)
    db_password_b64   = base64encode(var.db_password)
    mail_username_b64 = base64encode(var.mail_username)
    mail_password_b64 = base64encode(var.mail_password)
    dd_api_key_b64    = base64encode(var.dd_api_key)
  })
  sensitive_fields = ["data"]
  depends_on       = [kubectl_manifest.namespace]
}

resource "kubectl_manifest" "deployment" {
  yaml_body = templatefile("${local.k8s_dir}/deployment.yaml", {
    docker_image = var.docker_image
  })

  # O provider gavinbunney/kubectl usa um timeout fixo de 10min no create.
  # Quando o rollout nao termina (pods em CrashLoop/Pending), o retry interno
  # estoura o mesmo contexto e o erro final vira o enganoso
  # "client rate limiter Wait returned an error: context deadline exceeded",
  # escondendo a causa real. A espera/verificacao do rollout fica no job
  # `verify` (kubectl rollout status + smoke test), que mostra pods/eventos/logs.
  wait_for_rollout = false

  depends_on = [
    kubectl_manifest.serviceaccount,
    kubectl_manifest.configmap,
    kubectl_manifest.secret_db,
  ]
}

resource "kubectl_manifest" "service" {
  yaml_body = templatefile("${local.k8s_dir}/service.yaml", {
    node_port = var.node_port
  })
  depends_on = [kubectl_manifest.deployment]
}

resource "kubectl_manifest" "hpa" {
  yaml_body  = file("${local.k8s_dir}/hpa.yaml")
  depends_on = [kubectl_manifest.deployment]
}

resource "kubectl_manifest" "pdb" {
  yaml_body  = file("${local.k8s_dir}/pdb.yaml")
  depends_on = [kubectl_manifest.deployment]
}
