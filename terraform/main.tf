terraform {
  required_providers {
    mongodbatlas = {
      source  = "mongodb/mongodbatlas"
      version = "~> 1.14"
    }
  }
  required_version = ">= 1.0"
}

provider "mongodbatlas" {
  public_key  = var.mongodb_atlas_public_key
  private_key = var.mongodb_atlas_private_key
}

resource "mongodbatlas_project" "franchise_project" {
  name   = var.project_name
  org_id = var.org_id
}

resource "mongodbatlas_cluster" "franchise_cluster" {
  project_id = mongodbatlas_project.franchise_project.id
  name       = "franchise-cluster"

  cluster_type = "REPLICASET"
  replication_specs {
    num_shards = 1
    regions_config {
      region_name     = "SA_EAST_1"
      electable_nodes = 3
      priority        = 7
      read_only_nodes = 0
    }
  }

  cloud_backup                 = false
  auto_scaling_disk_gb_enabled = false

  provider_name               = "TENANT"
  backing_provider_name       = "AWS"
  provider_region_name        = "SA_EAST_1"
  provider_instance_size_name = "M0"
}

resource "mongodbatlas_database_user" "franchise_user" {
  username           = var.db_username
  password           = var.db_password
  project_id         = mongodbatlas_project.franchise_project.id
  auth_database_name = "admin"

  roles {
    role_name     = "readWrite"
    database_name = "franchisedb"
  }
}

resource "mongodbatlas_project_ip_access_list" "allow_all" {
  project_id = mongodbatlas_project.franchise_project.id
  cidr_block = "0.0.0.0/0"
  comment    = "Allow all IPs for development"
}