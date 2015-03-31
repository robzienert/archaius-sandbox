import static ratpack.groovy.Groovy.groovyTemplate
import static ratpack.groovy.Groovy.ratpack

import com.netflix.config.DynamicPropertyFactory
import com.robzienert.ArchaiusModule
import ratpack.groovy.template.TextTemplateModule

ratpack {

	bindings {
		add new ArchaiusModule()
		add TextTemplateModule
}

	handlers {
		get {
			String color = DynamicPropertyFactory.instance.getStringProperty('com.robzienert.SandboxConfig.color', '#123456').get()
			render groovyTemplate('index.html', color: color)
		}
		assets "public"
	}
}
