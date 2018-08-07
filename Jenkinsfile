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
                    
                    
                    repoUrl= gitRepoURL()
                    branchName = gitBranchName()
                    ispr = isGitPRBranch()
                      echo "${repoUrl} ${branchName} ${ispr}" 
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
                                        }
                                        
                                        def TESTRESULT=sh script: './gradlew test',returnStatus: true
                                         
                                        if (isGitPRBranch()) {
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

        stage('Lint') {
 
            steps {
               
               script {
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:Lint","Pending","PENDING")
                                        }
                                        sh './gradlew lint'
                                         
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:Lint","Completed","SUCCESS")
                                        }
                                    }

            }
        
        }

            stage('PMD') {
 
            steps {
               
              script {
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:PMD","Pending","PENDING")
                                        }

                                        sh './gradlew pmdMain'
                                         
                                        
                                         if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:PMD","Completed","SUCCESS")
                                        }
                                    }
                                    
            }
        
        }

        stage('CheckStyle') {
 
            steps {
               
                script {
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:CheckStyle","Pending","PENDING")
                                        }
                                        sh './gradlew check'
                                       
                                         if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:CheckStyle","Completed","SUCCESS")
                                        }
                                    }
                                    
            }
        
        }

                stage('FindBugs') {
 
            steps {
               
           script {
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:FindBugs","Pending","PENDING")
                                        }
                                        sh './gradlew findbugsMain'
                                       
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:FindBugs","Completed","SUCCESS")
                                        }
                                    }
                                    
            }
        
        }


        stage('Coverage') {
 
            steps {
               
        script {
                                        if (isGitPRBranch()) {
                                        setGithubStatus("continuous-integration/jenkins:Sonar","Pending","PENDING")
                                        
                                         sh './gradlew sonarqube -Dsonar.host.url=http://sonar.steadystatecd.com -Dsonar.login=3363177bf3a2cac9faffb0cbe292e94beb717021'
                                            
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
                sh 'docker build . -t 550522744793.dkr.ecr.us-east-1.amazonaws.com/userapi:${BUILD_NUMBER}'
                sh 'docker tag 550522744793.dkr.ecr.us-east-1.amazonaws.com/userapi:${BUILD_NUMBER} 550522744793.dkr.ecr.us-east-1.amazonaws.com/userapi:latest'
                sh '/home/jenkins/ecr-login.sh | /bin/bash '
                sh 'docker push 550522744793.dkr.ecr.us-east-1.amazonaws.com/userapi:${BUILD_NUMBER}'
                sh 'docker push 550522744793.dkr.ecr.us-east-1.amazonaws.com/userapi:latest'
            }
        }

        stage('Dev Deploy') {

                when {
                branch 'master'
            }

            steps {

              sh 'ecs-deploy -c dev-APICluster -n userapi -i 550522744793.dkr.ecr.us-east-1.amazonaws.com/userapi:${BUILD_NUMBER} -r us-east-1 --timeout 420 '

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
                branch 'master'
            }
           
            steps {
                    sh 'ecs-deploy -c qa-APICluster -n userapi -i 550522744793.dkr.ecr.us-east-1.amazonaws.com/userapi:${BUILD_NUMBER} -r us-east-1 --timeout 420 '

            }
        
        }

        stage('QA Functional Tests') {
            when {
                branch 'master'
            }
            steps {
                echo 'Unit Test'
            }
        }


         stage('Performance TEST') {
            when {
                branch 'master'
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
           
            publishHTML (target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: 'build/reports/checkstyle',
                    reportFiles: '*.html',
                    reportName: "CheckStyle Report"
                    ])
               publishHTML (target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: 'build/reports/findbugs',
                    reportFiles: '*.html',
                    reportName: "Findbugs Report"
                    ])
                   publishHTML (target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: 'build/reports/pmd',
                    reportFiles: '*.html',
                    reportName: "PMD Report"
                    ])

                        publishHTML (target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: 'build/reports',
                    reportFiles: 'dependency-check-report.html',
                    reportName: "Dependency Check Report"
                    ])
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
