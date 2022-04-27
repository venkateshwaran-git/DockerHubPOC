def folder = 'Poc/demo'

//This is the main container build
job("$folder/build_demo") {
    label('autoscale')
    logRotator {
        daysToKeep(10)
    }
    multiscm {
        git {
            remote {
                name('origin')
                url('ssh://vpalanik@stash.ezesoft.net/scm/~vpalanik/dockerhubratelimitdemopoc.git')
                credentials('StashCredentials')
            }
            branch('master')
            extensions {
                submoduleOptions {
                    //get latest submodules
                    tracking(true)
                }
            }
        }
    }
    triggers {
        scm('H/10 * * * *')
    }
    wrappers {
        preBuildCleanup()
        buildInDocker {
            registryCredentials('DockerHubCredentials')
            dockerfile('./slave')
            volume('/var/run/docker.sock', '/var/run/docker.sock')
            userGroup('2000')
            verbose()
        }
    }
    steps {
        // Pull the latest jenkins-scripts
        shell('if [ -d $scripts ]; then rm -rf $scripts; fi && git clone $scripts_repo')

        // Copy build files into local GOPATH in slave container
        shell('mkdir -p /go/src/stash.ezesoft.net/emd/bbg-secret && cp -r $WORKSPACE/* /go/src/stash.ezesoft.net/emd/bbg-secret')

        // Build and install
        shell('PATH=$PATH:/usr/local/go/bin CGO_ENABLED=0 GOOS=linux GOARCH=amd64 GO111MODULE=off /usr/local/go/bin/go build -a -installsuffix cgo -ldflags="-w -s" -o $WORKSPACE/bbg-secret /go/src/stash.ezesoft.net/emd/bbg-secret/src/cmd/bbg-secret')

        //unit tests and coverage
        shell('bash build-scripts/prep-coverage-report.sh')

        // Build and upload the container
        shell('bash jenkins-scripts/build-container.sh')

    }
  

 
}