# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|

  # Every Vagrant virtual environment requires a box to build off of.
  config.vm.box='atlassiandev/connect'

  config.vm.network "forwarded_port", guest: 3000, host: 3000
  config.vm.network "forwarded_port", guest: 8000, host: 8000
  config.vm.network "forwarded_port", guest: 2990, host: 2990
  config.vm.network "forwarded_port", guest: 1990, host: 1990


  # Required for NFS to work, pick any local IP
  config.vm.network "private_network", ip: "192.168.50.50"
  # Use NFS for shared folders for better performance
  config.vm.synced_folder ".", "/vagrant", type: "nfs"
  
  config.vm.provider "virtualbox" do |v|
    # Originally from Atlassian is 1/2 memory and full cpu, overridden as unnecessary
    cpus = 2
    mem = 1024

    v.customize ["modifyvm", :id, "--memory", mem]
    v.customize ["modifyvm", :id, "--cpus", cpus]
    v.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
    v.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
  end

end
