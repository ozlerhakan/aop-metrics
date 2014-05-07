### Master Thesis
---

The aim of my master project is to analyse the existing **[AspectJ](http://eclipse.org/aspectj/)** applications. In order to collect proper information, I have implemented several metrics consisting of **[aspect-oriented programming](http://en.wikipedia.org/wiki/Aspect-oriented_programming) (AOP)** and **[object-oriented programming](http://en.wikipedia.org/wiki/Object-oriented_programming) (OOP)** features.

### The Hierarchy of The Research Questions 
---

1.	How large is the system?
	*	*Lines of Code* [(LOC)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L156)
	*	*Vocabulary Size* [(VS)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L191) 
	*	Number of Attributes* [(NOA)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L201) 
	*	*Number of Methods* [(NOM)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L211)
2.	How often are AOP constructs used compared to OOP features?
	*	*Number of Intertype* [(NOI)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L238)
	*	*Number of Advices* [(NOAd)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L256)
3.	Which AOP constructs are typically used?
	*	*Inherited Aspects* [(InA)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L573)
	*	*Singleton Aspects* [(SA)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L580)
	*	*Non-Singleton Aspects* [(nSA)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L589)
	*	*Advice-Advance Pointcut Dependence* [(AAP)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L557)
	*	*Advice-Basic Pointcut Dependence* [(ABP)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L567)
	*	*Number of Around Advice* [(NOAr)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L733)
	*	*Number of Before/After Advice* [(NOBA)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L752)
	*	*Number of After Throwing/Returning Advice* [(NOTR)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L770)
	*	*Number of Call* [(NOC)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L893)
	*	*Number of Execution* [(NOE)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L900)
	*	*Number of AdviceExecution* [(AE)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L596)
	*	*Number of Wildcards* [(NOW)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L1128)
	*	*Number of non-Wildcards* [(NOnW)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L1140)
	*	*Argument size of Args-Advice* [(AAd)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L722)
	*	*Argument size of Args-Advice-args* [(AAda)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L727)
4.	What percentage of a system is advised by AOP?
	*	*Number of Advised Classes* [(AdC)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L851)
	*	*Number of non-Advised Classes* [(nAdC)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L922)
	*	*Number of Advised Methods* [(AdM)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L987)
	*   *Number of non-Advised Methods* [(nAdM)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L993)
	*	*Classes and Subclasses* [(CsC)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L1046)
	*	*Average of Subclasses of Classes* [(ScC)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L1076)
5.	Is there a connection between the amount of coupling in an aspect, and how many shadows it advises?
	*	*Advice-Join Point Shadow Dependence* [(AJ)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L655)
	*	*Number of thisJoinPoint/Static* [(tJPS)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L1154)
	*	*Number of Modified Args* [(MoA)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L1225)
	*	*Number of Accessed Args* [(AcA)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L1262)
	*	*Around Advice - non-Proceed Call Dependence* [(AnP)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L796)
6.	How many dependencies are there between classes and aspects?	
	*	*Attribute-Class Dependence Measure* [(AtC)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L366)
	*	*Advice-Class Dependence* [(AC)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L380)
	*	*Intertype-Class Dependence* [(IC)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L414)
	*	*Method-Class Dependence* [(MC)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L448)
	*	*Pointcut-Class Dependence* [(PC)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L481)
	*	*Advice-Method Dependence* [(AM)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L332) 
	*	*IntertypeMethod-Method Dependence* [(IM)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L340)
	*	*Method-Method Dependence* [(MM)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L356)
	*	*Pointcut-Method Dependence* [(PM)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L496) 


### Have a look at an example
---
One of the questions we examine is: how many aspects extend to an abstract aspect in a given aspect-oriented project?

The Metric representation of the question is: the number of inherited aspects in a given aspect-oriented project.

```Clojure
 (defn NOInheritedAspects [?aspectname ?abstractname]
         (l/fresh [?aspect ?source ?super]
                   (NOAspects ?aspect ?source)
                   (w/aspect-declaredsuper ?aspect ?super)
                   (equals ?aspectname (str "Aspect {"(.getSimpleName ?aspect)"}"))
                   (equals ?abstractname (str "From Abstract Aspect -> "(.getSimpleName ?super)))
                   (succeeds (.isAbstract ?super))))
```

### How the metrics work
---

First of all, make sure that you have all dependencies about the Ekeko Plugin in your Eclipse IDE, if not, you have to download it with its dependencies from [here](https://github.com/cderoove/damp.ekeko/wiki/Getting-Started-with-Ekeko). 

In addition, after importing the Ekeko as an Eclipse project, you have to import the Ekeko's AspectJ extension in order to complete all essential parts. The link of the extension is [here](https://github.com/cderoove/damp.ekeko.aspectj).

Now, you can get the code of the aop.metrics-clj (Ekeko AJFX) by simply cloning the project, plus import it within the workspace of your Eclipse IDE.

