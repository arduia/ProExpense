package com.arduia.processor

import com.arduia.annotation.DateInt
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.ElementFilter
import javax.tools.Diagnostic

@SupportedAnnotationTypes("com.arduia.annotation.DateInt")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class DateIntProcessor:AbstractProcessor(){

    override fun process(p0: MutableSet<out TypeElement>?, p1: RoundEnvironment?): Boolean {

        val annotatedElements = p1?.getElementsAnnotatedWith(DateInt::class.java)
        val fields = ElementFilter.fieldsIn(annotatedElements)
        fields.forEach {
            if(it.asType().kind.isPrimitive){
               (it.constantValue as? Int)?.let { value ->
                   if(value !in 1..7){
                       val name = it.simpleName.toString()
                       showError("The value of $name  Should Be Between 1 and 7 ",it)
                   }
                }
            }
        }

       return true
    }

    override fun init(p0: ProcessingEnvironment?) {
        super.init(p0)


    }

    private fun showError(msg:String, element:Element? = null){
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR,msg,element)
    }
    override fun getSupportedAnnotationTypes(): MutableSet<String>  =
        mutableSetOf(DateInt::class.java.canonicalName)

}
