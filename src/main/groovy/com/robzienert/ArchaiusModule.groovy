package com.robzienert

import com.google.common.base.Strings
import com.netflix.config.ConfigurationManager
import com.netflix.config.DynamicWatchedConfiguration
import com.netflix.config.source.ZooKeeperConfigurationSource
import groovy.util.logging.Slf4j
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import ratpack.guice.ConfigurableModule

@Slf4j
class ArchaiusModule extends ConfigurableModule<SandboxConfig> {

	protected void configure() {
		zookeeperConfigSource()

		String app = ConfigurationManager.deploymentContext.applicationId
		String env = ConfigurationManager.deploymentContext.deploymentEnvironment

		if (Strings.isNullOrEmpty(app)) {
			log.warn('System property "archaius.deployment.applicationId" is empty')
		}
		if (Strings.isNullOrEmpty(env)) {
			log.warn('System property "archaius.deployment.environment" is empty')
		}
		log.info("Color: ${ConfigurationManager.configInstance.getString('com.robzienert.SandboxConfig.color')}")
	}

	private zookeeperConfigSource() {
		String zkConfigRootPath = '/archaius/config'
		CuratorFramework client = CuratorFrameworkFactory.newClient(
				'192.168.111.221:2181',
				new ExponentialBackoffRetry(1000, 3)
		)

		ZooKeeperConfigurationSource zkConfigSource = new ZooKeeperConfigurationSource(client, zkConfigRootPath)
		zkConfigSource.start()

		DynamicWatchedConfiguration zkDynamicConfig = new DynamicWatchedConfiguration(zkConfigSource)
		ConfigurationManager.install(zkDynamicConfig)
	}
}
