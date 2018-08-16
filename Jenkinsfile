pipeline {
    agent any

    options {
        skipDefaultCheckout() 
        timeout(time: 10, unit: 'MINUTES') 
        timestamps() 
        
    }
    
    

    stages {
        
        

      stage('Checkout') {
            steps {
                checkout scm

            }
      }

        stage('Build') {
            steps {

                
                echo 'Build'
                script {
                    sh 'printenv'
                    
                    repoUrl= gitRepoURL()
                    branchName = gitBranchName()
                    ispr = isGitPRBranch()
                echo "${repoUrl} ${branchName} ${ispr}" 
                }

            }
        }

        stage('Unit Test') {
            steps {
                 parallel(
                        "UnitTest ": {
                            echo 'Run Units tests'
                                   script {
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:Unit test","Pending","PENDING")
                                        sleep 60 
                                        setGithubStatus("continuous-integration/jenkins:Unit test","Completed","SUCCESS")
                                        }
                                    }
                        },
                         "Security Test ": {
                            echo 'Run integration testing'
                        }
                        
                )
            }
        }         

        stage('Static Code Analysis') {
            steps {
               
                parallel(
                        "PMD ": {
                            echo 'Run integration tests'
                                   script {
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:PMD","Pending","PENDING")
                                        sleep 60 
                                        setGithubStatus("continuous-integration/jenkins:PMD","Completed","SUCCESS")
                                        }
                                    }
                            
                        },
                         "CheckStyle ": {
                                   script {
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:CheckStyle","Pending","PENDING")
                                        sleep 60 
                                        setGithubStatus("continuous-integration/jenkins:CheckStyle","Completed","SUCCESS")
                                        }
                                    }
                        },
                         "FindBugs ": {
                                   script {
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:FindBugs","Pending","PENDING")
                                        sleep 60 
                                        setGithubStatus("continuous-integration/jenkins:FindBugs","Completed","SUCCESS")
                                        }
                                    }
                        },
                        "Sonar Scan": {
                                   script {
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:Sonar","Pending","PENDING")
                                        sleep 60 
                                        setGithubStatus("continuous-integration/jenkins:Sonar","Completed","SUCCESS")
                                        }
                                    }
                        },
                        "OWASP Check": {
                                   script {
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:OWASP","Pending","PENDING")
                                        sleep 60 
                                        setGithubStatus("continuous-integration/jenkins:OWASP","Completed","SUCCESS")
                                        }
                                    }
                        }
                        
                )
            }
        }

        stage('Build Docker') {
            when {
                branch 'develop'
            }
            steps {
                echo 'Build Docker Image'
            }
        }

        stage('Dev Deploy') {

                when {
                branch 'develop'
            }

            steps {
                echo 'Deploy DEV'
                echo 'Sanity Checks'
            }
        
        }

        stage('DEV Test') {
            when {
                branch 'develop'
            }
            steps {
                         
                parallel(
                        "Integration Test ": {
                            echo 'Run integration tests'
                        },
                         "Functional Test ": {
                            echo 'Run integration tests'
                        }
                        
                )
            }
        }

         stage('QA deploy') {
             when {
                branch 'develop'
            }
           
            steps {
                echo 'Deploy QA'
                echo 'Sanity Checks'
            }
        
        }

        stage('QA Functional Tests') {
            when {
                branch 'develop'
            }
            steps {
                echo 'Unit Test'
            }
        }


         stage('Performance TEST') {
            when {
                branch 'develop'
            }
            steps {
                echo 'Deploy QA'
                echo 'Sanity Checks'
            }
        
        }

           
        stage('Deploy Stage') {
           when {
                branch 'master'
            }
            steps {
                echo 'deploy stage'
                echo ' Stage Sanity Checks'
            }
        }
    }
    post { 
        always { 
            echo 'Always'
            
        }

        failure { 
            echo 'Failed'
        }

        success { 
            echo 'Success!'

                script {
                    if (isGitPRBranch()) {

                            
                            sendSlackNotification("SUCCESS","",true)
                            
                            }
                }

        }

        unstable { 
            echo 'Unstable'
        }

    }
}
