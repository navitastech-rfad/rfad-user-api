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
                  
                }

            }
        }

        stage('Unit Test') {
            steps {
                 parallel(
                        "UnitTest ": {
                            echo 'Run Units tests'
                                 
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
                            "Lint  ": {
                            echo 'Run Lint'
                                    
                            
                        },
                        "PMD ": {
                            echo 'Run PMD'
                                    
                            
                        },
                         "CheckStyle ": {
                              echo 'Run CheckStyle'
                                    
                        },
                         "FindBugs ": {

                             echo 'Run FindBugs'
                                    
                        },
                        "Sonar Scan": {
                                     echo 'Sonar Scan'
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

                 
        }

        unstable { 
            echo 'Unstable'
        }

    }
}
