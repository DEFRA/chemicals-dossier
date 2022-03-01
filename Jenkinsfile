@Library('jenkins-shared-library')_
def helper = new helpers.PipelineHelper(this);

node (label: 'autoSlaveLive') {
  def secrets = [
    [envVariable: 'AZURE_STORAGE_CONNECTION_STRING', name: 'azureStorageConnectionString', secretType:'Secret'],
    [envVariable: 'AZURE_TMP_STORAGE_CONNECTION_STRING', name: 'azureTmpStorageConnectionString', secretType:'Secret'],
    [envVariable: 'DATABASE_DB_PASSWORD', name: 'sndSqlDBAdminPass', secretType:'Secret']
    ]

  def URL = "reach-dossier"
  def RESOURCE = "SNDCHMINFRGP001-${URL}-${helper.getEnvSuffix()}"
  def AI = "SNDCHMINFRGP001-${helper.getEnvSuffix()}"
  def DATABASE_HOST_NAME = "${helper.getDatabaseServerName()}.database.windows.net"
  def DATABASE_DB_PORT = 1433
  def APP = "${URL}-${helper.getEnvSuffix()}"
  def DATABASE_DB_USER = "SA-AZURE-CHM-SQL-SND@Defra.onmicrosoft.com"
  def KEY_VAULT_NAME = "SECCHMINFKVT001"
  def DATABASE_ADMIN_PASS_NAME_KEY_LOOKUP = "sndSqlDBAdminPass"
  def AZURE_STORAGE_CONTAINER_NAME = "reach-develop"
  def AZURE_TMP_STORAGE_CONTAINER_NAME = "reach-tmp-develop"
  def DB_URL = "jdbc:sqlserver://${DATABASE_HOST_NAME}:${DATABASE_DB_PORT};database=$APP;encrypt=true;schema=dbo;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;authentication=ActiveDirectoryPassword;loginTimeout=30"

  withAzureKeyvault(secrets) {
    def envArray = [
      "APP_NAME=${APP}",
      "SERVICE_NAME=REACH Dossier",
      "URL_PATH=${URL}",
      "RESOURCE_GROUP=${RESOURCE}",
      "BACKEND_PLAN=SNDCHMINFRGP001-${URL}-${helper.getEnvSuffix()}-service-plan",
      "AI_NAME=${AI}",
      "ACR_REPO=reach-dossier/reach-dossier",
      "SET_APP_LOGGING=false",
      "RUN_SONAR=true",
      "PROJECT_REPO_URL=https://giteux.azure.defra.cloud/chemicals/reach-dossier.git",
      "DB_URL=${DB_URL}",
      "DB_USER=${DATABASE_DB_USER}",
      "DB_PASSWORD=${helper.getKeyVaultPassword(KEY_VAULT_NAME, DATABASE_ADMIN_PASS_NAME_KEY_LOOKUP)}",
      "CONNECTION_STRING=HTTP_REACH_DOSSIER_PORT=8080 WEBSITES_PORT=8080 JWT_SECRET_KEY='MySecretKey' DB_REACH_DOSSIER_URL='${DB_URL}' DB_USER='${DATABASE_DB_USER}' DB_PASSWORD='${helper.getKeyVaultPassword(KEY_VAULT_NAME, DATABASE_ADMIN_PASS_NAME_KEY_LOOKUP)}' AZURE_STORAGE_CONNECTION_STRING='${AZURE_STORAGE_CONNECTION_STRING}' AZURE_STORAGE_CONTAINER_NAME='${AZURE_STORAGE_CONTAINER_NAME}' AZURE_TMP_STORAGE_CONNECTION_STRING='${AZURE_TMP_STORAGE_CONNECTION_STRING}' AZURE_TMP_STORAGE_CONTAINER_NAME='${AZURE_TMP_STORAGE_CONTAINER_NAME}' TEMP_DOSSIER_DAYS_TO_EXPIRE=1",
      "DB_REACH_DOSSIER_TEST_URL=jdbc:sqlserver://${DATABASE_HOST_NAME}:${DATABASE_DB_PORT};database=reach-dossier-unit-test-${helper.getEnvSuffix()};user=${DATABASE_DB_USER};password=${DATABASE_DB_PASSWORD};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;authentication=ActiveDirectoryPassword"
    ]

    withEnv(envArray) {
      def CREATE_DB = [APP, "reach-dossier-unit-test-${helper.getEnvSuffix()}"]
      reachPipeline(CREATE_DB)
    }
  }
}
