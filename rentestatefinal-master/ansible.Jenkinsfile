pipeline {
    agent any

    parameters {
        booleanParam(name: 'INSTALL_POSTGRES', defaultValue: true, description: 'Install PostgreSQL')
        booleanParam(name: 'INSTALL_SPRING', defaultValue: true, description: 'Install Spring Boot Application')
    }

    environment {
        // Updated paths to match your actual Ansible project structure
        ANSIBLE_CONFIG = "${WORKSPACE}/ansible-job/ansible-devops-2025/ansible.cfg"
        ANSIBLE_INVENTORY = "${WORKSPACE}/ansible-job/ansible-devops-2025/hosts.yaml"
        ANSIBLE_PLAYBOOKS = "${WORKSPACE}/ansible-job/ansible-devops-2025/playbooks"
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
                // List workspace to verify paths
                sh 'ls -la ${WORKSPACE}'
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
                        ansible-playbook -i ${ANSIBLE_INVENTORY} -l dbservers ${ANSIBLE_PLAYBOOKS}/postgres-16.yaml
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
                        ansible-playbook -i ${ANSIBLE_INVENTORY} -l appservers ${ANSIBLE_PLAYBOOKS}/spring.yaml \
                            -e "app_jar_path=${WORKSPACE}/target/rentEstate-0.0.1-SNAPSHOT.jar"
                    """
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
