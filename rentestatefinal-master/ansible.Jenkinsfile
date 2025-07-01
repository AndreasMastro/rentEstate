pipeline {
    agent any

    parameters {
        booleanParam(name: 'INSTALL_POSTGRES', defaultValue: true, description: 'Install PostgreSQL')
        booleanParam(name: 'INSTALL_SPRING', defaultValue: true, description: 'Install Spring Boot Application')
    }

    environment {
        ANSIBLE_CONFIG = "${WORKSPACE}/ansible-devops-2025/ansible.cfg"
        ANSIBLE_INVENTORY = "${WORKSPACE}/ansible-devops-2025/hosts.yaml"
    }

    stages {
        stage('Checkout SCM') {
            steps {
                checkout scm
            }
        }

        stage('Verify Ansible Installation') {
            steps {
                sh 'ansible --version'
            }
        }

        stage('Test Connection to Servers') {
            steps {
                script {
                    sh """
                        ansible all -i ${ANSIBLE_INVENTORY} -m ping
                    """
                }
            }
        }

        stage('Install PostgreSQL') {
            when {
                expression { return params.INSTALL_POSTGRES }
            }
            steps {
                script {
                    sh """
                        ansible-playbook -i ${ANSIBLE_INVENTORY} -l dbservers ${WORKSPACE}/ansible-devops-2025/playbooks/postgres-16.yaml
                    """
                }
            }
        }

        stage('Deploy Spring Boot Application') {
            when {
                expression { return params.INSTALL_SPRING }
            }
            steps {
                script {
                    // First build the application
                    dir('.') {
                        sh 'mvn clean package -DskipTests'
                    }
                    
                    // Then deploy using Ansible
                    sh """
                        ansible-playbook -i ${ANSIBLE_INVENTORY} -l appservers ${WORKSPACE}/ansible-devops-2025/playbooks/spring.yaml \
                            -e "app_jar_path=${WORKSPACE}/target/rentEstate-0.0.1-SNAPSHOT.jar"
                    """
                }
            }
        }
    }

    post {
        always {
            // Clean up workspace after build
            cleanWs()
        }
        failure {
            // Send notification on failure
            echo 'Pipeline failed!'
        }
    }
}