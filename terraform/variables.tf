variable "mongodb_atlas_public_key" {
  description = "MongoDB Atlas public API key"
  type        = string
  sensitive   = true
}

variable "mongodb_atlas_private_key" {
  description = "MongoDB Atlas private API key"
  type        = string
  sensitive   = true
}

variable "org_id" {
  description = "MongoDB Atlas organization ID"
  type        = string
}

variable "project_name" {
  description = "MongoDB Atlas project name"
  type        = string
  default     = "franchise-project"
}

variable "db_username" {
  description = "Database username"
  type        = string
  default     = "franchiseuser"
}

variable "db_password" {
  description = "Database password"
  type        = string
  sensitive   = true
}