## install and configure Kamal 2

### install ruby
```sh
# Install Xcode Command Line Tools
$ xcode-select --install

# Install Homebrew and dependencies
$ /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
$ echo 'export PATH="/opt/homebrew/bin:$PATH"' >> ~/.zshrc
$ source ~/.zshrc
$ brew install openssl@3 libyaml gmp rust

# Install Mise version manager
$ curl https://mise.run | sh
$ echo 'eval "$(~/.local/bin/mise activate)"' >> ~/.zshrc
$ source ~/.zshrc

# Install Ruby globally with Mise
$ mise use -g ruby@3
```

### Set Up Multipass VM
Install Multipass:
```sh
brew install --cask multipass
```

Launch a Multipass VM: Create a new instance with sufficient resources:
```sh
multipass launch -n spring-vm --cpus 2 --memory 2G --disk 10G
```

To check the status of all Multipass VMs, run:
```sh
multipass list
```
To stop a running VM:
```sh
multipass stop spring-vm
```
Replace spring-vm with the name of your VM.

To start the VM again:
```sh
multipass start spring-vm
```

To restart a Multipass VM
```sh
multipass restart spring-vm
```
To delete a Multipass VM (Permanent Action)
⚠ Warning: Deleting a VM removes all data inside it.
```sh
multipass delete spring-vm
```
The VM will be marked for deletion but not removed yet.

To permanently remove it from your system, run:
```sh
multipass purge
```

Get the IP Address of the VM:
```sh
multipass info spring-vm
```
Note the IP address (e.g., 192.168.64.2).

Access the VM:
```sh
multipass shell spring-vm
```

 Install SSH on the VM:
```sh
sudo apt update
sudo apt install -y openssh-server
sudo systemctl enable ssh
sudo systemctl start ssh
```

Verify that SSH is running:
```sh
sudo systemctl status ssh
```

Get the Multipass VM’s IP Address
On your local machine, run:
```sh
multipass list
```
You should see an output similar to this:

```sh
Name                    State             IPv4
spring-vm               Running           192.168.64.2
```
Take note of the IP address (e.g., 192.168.64.2).
#### Configure SSH Access
##### Option 1: Use Password Authentication (Not Recommended for Security)
If you want to log in using a password, set a password for the default ubuntu user inside the VM:

```sh
sudo passwd ubuntu
```
Then, from your local machine, SSH into the VM:

```sh
ssh ubuntu@192.168.64.2
```
##### Option 2: Set Up SSH Key-Based Authentication (Recommended)
Generate an SSH Key (If You Don’t Have One) On your local machine:

```sh
ssh-keygen -t rsa -b 4096
```
Press Enter to use the default location (`~/.ssh/id_rsa`).

Copy Your Public Key to the Multipass VM Run the following command on your local machine:

```sh
multipass transfer ~/.ssh/id_rsa.pub spring-vm:/home/ubuntu/
```
Inside the VM, configure the SSH key:

```sh
multipass shell spring-vm
mkdir -p ~/.ssh
cat ~/id_rsa.pub >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
chmod 700 ~/.ssh
rm ~/id_rsa.pub
```
Restart SSH Service
```sh
sudo systemctl restart ssh
```
Test SSH Access From your local machine:
```sh
ssh ubuntu@192.168.64.2
```
If you set up SSH keys correctly, you should log in without entering a password.