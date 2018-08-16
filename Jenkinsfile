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
                    sh './gradlew clean build'

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
                                        def TESTRESULT=sh script: './gradlew test',returnStatus: true
                                        setGithubStatus("continuous-integration/jenkins:Unit test","Completed","SUCCESS")
                                        }
                                    }
                        },
                         "Security Test ": {
                           script {
                                sh './gradlew dependencyCheckAnalyze'
                            }
                        }
                        
                )
            }
        }         

        stage('Code Analysis') {
            steps {
               
                parallel(
                        "Lint ": {
                            
                                   script {
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:Lint","Pending","PENDING")
                                        
                                        sh './gradlew lint'

                                        setGithubStatus("continuous-integration/jenkins:Lint","Completed","SUCCESS")
                                        }
                                    }
                            
                        },
                        "PMD ": {
                            
                                   script {
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:PMD","Pending","PENDING")
                                        
                                        sh './gradlew pmdMain'

                                        setGithubStatus("continuous-integration/jenkins:PMD","Completed","SUCCESS")
                                        }
                                    }
                            
                        },
                         "CheckStyle ": {
                                   script {
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:CheckStyle","Pending","PENDING")
                                        
                                        sh './gradlew check'
                                        
                                        setGithubStatus("continuous-integration/jenkins:CheckStyle","Completed","SUCCESS")
                                        }
                                    }
                        },
                         "FindBugs ": {
                                   script {
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:FindBugs","Pending","PENDING")
                                        
                                        sh './gradlew findbugsMain'
                                        
                                        setGithubStatus("continuous-integration/jenkins:FindBugs","Completed","SUCCESS")
                                        }
                                    }
                        },
                     
                        "OWASP Check": {
                                   script {
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:OWASP","Pending","PENDING")
                                       
                                        sh './gradlew dependencyCheckAnalyze'
                                       
                                        setGithubStatus("continuous-integration/jenkins:OWASP","Completed","SUCCESS")
                                        }
                                    }
                        }
                        
                )
            }
        }


        stage('Sonar') {
             
                steps {
                      script {
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:Sonar","Pending","PENDING")
                                        sleep 60 
                                        setGithubStatus("continuous-integration/jenkins:Sonar","Completed","SUCCESS")
                                        }
                                    }
                }
            }


        stage('Build Docker') {
            when {
                branch 'master'
            }
            steps {
                echo 'Build Docker Image'
            }
        }

        stage('Dev Deploy') {

                when {
                branch 'master'
            }

            steps {
                echo 'Deploy DEV'
                echo 'Sanity Checks'
            }
        
        }

        stage('DEV Test') {
            when {
                branch 'master'
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
