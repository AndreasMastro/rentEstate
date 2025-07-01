pipeline {
    agent any

    environment {
        // Paths relative to workspace
        ANSIBLE_DIR = "${WORKSPACE}/ansible/ansible-devops-2025"
        ANSIBLE_CONFIG = "${WORKSPACE}/ansible/ansible-devops-2025/ansible.cfg"
        ANSIBLE_INVENTORY = "${WORKSPACE}/ansible/ansible-devops-2025/hosts.yaml"
        ANSIBLE_PLAYBOOKS = "${WORKSPACE}/ansible/ansible-devops-2025/playbooks"
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
                        ansible -i ${ANSIBLE_INVENTORY} appservers,dbservers -m ping
                    """
                }
            }
        }

        stage('Deploy Docker Compose Application') {
            steps {
                script {
                    sh """
                        ansible-playbook -i ${ANSIBLE_INVENTORY} -l appservers,dbservers ${ANSIBLE_PLAYBOOKS}/springdocker.yaml \
                            -e "appdir=/opt/rentEstate" \
                            -e "branch=main"
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
        success {
            echo 'Pipeline completed successfully! Application deployed via Docker Compose.'
        }
    }
}