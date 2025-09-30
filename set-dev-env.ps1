# This script sets up the environment variables and directories required for local development on Windows.
# It creates a persistent environment variable `IMAGE_STORAGE_PATH` for the current user.
# It also checks and creates the C:\pet-manage directory if it doesn't exist.
# To run this script, open PowerShell as an administrator, navigate to the project root,
# and execute: .\set-dev-env.ps1

$VarName = "IMAGE_STORAGE_PATH"
$VarValue = "C:\AppData\pet-manage\storage"
$DirectoryPath = "C:\AppData\pet-manage\storage"

Write-Host "Setting up development environment..."
Write-Host "Target directory: $DirectoryPath"
Write-Host "Environment variable: $VarName = $VarValue"
Write-Host ""

# Check if the directory exists, create it if it doesn't
if (Test-Path $DirectoryPath) {
    Write-Host "Directory '$DirectoryPath' already exists."
} else {
    Write-Host "Creating directory '$DirectoryPath'..."
    try {
        New-Item -ItemType Directory -Path $DirectoryPath -Force
        Write-Host "Directory '$DirectoryPath' created successfully."
    }
    catch {
        Write-Host "ERROR: Failed to create directory '$DirectoryPath'."
        Write-Host "Error details: $($_.Exception.Message)"
        exit 1
    }
}

# Set the environment variable
Write-Host "Setting environment variable '$VarName' to '$VarValue'..."

try {
    # Set a persistent environment variable for the current user.
    [System.Environment]::SetEnvironmentVariable($VarName, $VarValue, "User")
    Write-Host ""
    Write-Host "SUCCESS: Environment variable '$VarName' has been set."
    Write-Host "Directory '$DirectoryPath' is ready."
    Write-Host "Please restart your terminal or IDE (like IntelliJ) for the changes to take effect."
}
catch {
    Write-Host ""
    Write-Host "ERROR: Failed to set the environment variable. Please make sure you have the necessary permissions."
    Write-Host "You might need to run this script as an administrator."
    Write-Host "Error details: $($_.Exception.Message)"
}