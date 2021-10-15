package pl.beone.promena.alfresco.module.connector.activemq.configuration.service

import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import org.springframework.web.context.support.XmlWebApplicationContext

@Service
class ContextProvider {

    companion object {
        private var context: XmlWebApplicationContext? = null


        fun getContext() : XmlWebApplicationContext? {
            return context;
        }
    }

    constructor(context: ApplicationContext) {
        ContextProvider.context = context as XmlWebApplicationContext
    }
}