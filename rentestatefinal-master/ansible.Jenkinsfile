pipeline {
    agent any

    parameters {
        booleanParam(name: 'INSTALL_POSTGRES', defaultValue: true, description: 'Install PostgreSQL')
        booleanParam(name: 'INSTALL_SPRING', defaultValue: true, description: 'Install Spring Boot Application')
    }

    environment {
        // Paths relative to workspace
        ANSIBLE_DIR = "${WORKSPACE}/ansible-job/ansible-devops-2025"
        ANSIBLE_CONFIG = "${WORKSPACE}/ansible-job/ansible-devops-2025/ansible.cfg"
        ANSIBLE_INVENTORY = "${WORKSPACE}/ansible-job/ansible-devops-2025/hosts.yaml"
        ANSIBLE_PLAYBOOKS = "${WORKSPACE}/ansible-job/ansible-devops-2025/playbooks"
    }

    stages {
        stage('Checkout Application Code') {
            steps {
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/main']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/AndreasMastro/rentEstate.git'
                    ]]
                ])
            }
        }

        stage('Checkout Ansible Code') {
            steps {
                dir('ansible') {
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: '*/main']],
                        userRemoteConfigs: [[
                            url: 'https://github.com/AndreasMastro/ansible.git'  
                        ]]
                    ])
                }
            }
        }

        stage('Verify Setup') {
            steps {
                sh 'ansible --version'
                sh 'ls -la ${WORKSPACE}'
                sh 'ls -la ${ANSIBLE_DIR}'
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
                    dir('rentestatefinal-master') {
                        sh 'mvn clean package -DskipTests'
                    }
                    sh """
                        ansible-playbook -i ${ANSIBLE_INVENTORY} -l appservers ${ANSIBLE_PLAYBOOKS}/spring.yaml \
                            -e "app_jar_path=${WORKSPACE}/rentestatefinal-master/target/rentEstate-0.0.1-SNAPSHOT.jar"
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