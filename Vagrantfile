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
    host = RbConfig::CONFIG['host_os']

    # Give VM 1/4 system memory & access 1/2 cpu cores on the host (originally from Atlassian is 1/2 memory and full cpu)
    if host =~ /darwin/
      cpus = `sysctl -n hw.ncpu`.to_i / 2
      # sysctl returns Bytes and we need to convert to MB
      mem = `sysctl -n hw.memsize`.to_i / 1024 / 1024 / 2 / 2
    elsif host =~ /linux/
      cpus = `nproc`.to_i / 2
      # meminfo shows KB and we need to convert to MB
      mem = `grep 'MemTotal' /proc/meminfo | sed -e 's/MemTotal://' -e 's/ kB//'`.to_i / 1024 / 2 / 2
    else # sorry Windows folks, I can't help you
      cpus = 2
      mem = 1024
    end

    v.customize ["modifyvm", :id, "--memory", mem]
    v.customize ["modifyvm", :id, "--cpus", cpus]
    v.customize ["modifyvm", :id, "--natdnshostresolver1", "on"]
    v.customize ["modifyvm", :id, "--natdnsproxy1", "on"]
  end

end
