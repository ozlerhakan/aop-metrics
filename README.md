**Table of Contents**  *generated with [DocToc](http://doctoc.herokuapp.com/)*

- [The Definitions of The Metrics](#the-definitions-of-the-metrics)

- [Have a look at an example](#have-a-look-at-an-example)

- [Quick Access](#quick-access)

- [How the metrics work?](#how-to-run-the-metrics)

### Master Thesis
---

The aim of my master project is to analyse the existing **[AspectJ](http://eclipse.org/aspectj/)** applications. In order to collect proper information, I have implemented several metrics consisting of **[aspect-oriented programming](http://en.wikipedia.org/wiki/Aspect-oriented_programming) (AOP)** and object-oriented programming **(OOP)** features.

### The Hierarchy of The Research Questions 
---

1.	How large is the system?
	*	Lines of Code (LOC) 
	*	Vocabulary Size (VS) 
	*	Number of Attributes (NOA) 
	*	Number of Methods (NOM)
2.	How often are AOP constructs used compared to OOP features?
	*	Number of Intertype (NOI)
	*	Number of Advices (NOAd)
3.	Which AOP constructs are typically used?
	*	Inherited Aspects (InA)
	*	Singleton Aspects (SA)
	*	Non-Singleton Aspects (nSA)
	*	Advice-Advance Pointcut Dependence (AAP)
	*	Advice-Basic Pointcut Dependence (ABP)
	*	Number of Around Advice (NOAr)
	*	Number of Before/After Advice (NOBA)
	*	Number of After Throwing/Returning Advice (NOTR)
	*	Number of Call (NOC)
	*	Number of Execution (NOE)
	*	Number of AdviceExecution (AE)
	*	Number of Wildcards (NOW)
	*	Number of non-Wildcards (NOnW)
	*	Argument size of Args-Advice (ASA)
	*	Argument size of Args-Advice-args (ASAA)
4.	What percentage of a system is advised by AOP?
	*	Number of Advised Classes (AdC)
	*	Number of non-Advised Classes (nAdC)
	*	Number of Advised Methods (AdM)
	*   Number of non-Advised Methods (nAdM)
	*	Classes and Subclasses (CsC)
	*	Average of Subclasses of Classes (ACsC)
5.	Is there a connection between the amount of coupling in an aspect, and how many shadows it advises?
	*	Advice-Join Point Shadow Dependence (AJ)
	*	Number of thisJoinPoint/Static (NOJPS)
	*	Number of Modified Args (MoA)
	*	Number of Accessed Args (AcA)
	*	Around Advice - non-Proceed Call Dependence (AnP)
6.	How many dependencies are there between classes and aspects?	
	*	Attribute-Class Dependence Measure (AtC)
	*	Advice-Class Dependence (AC)
	*	Intertype-Class Dependence (IC) 
	*	Method-Class Dependence (MC) 
	*	Pointcut-Class Dependence (PC) 
	*	Advice-Method Dependence (AM) 
	*	IntertypeMethod-Class Dependence (IM) 
	*	Method-Method Dependence (MM) 
	*	Pointcut-Method Dependence (PM) 


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

### Quick Access
---

The implemented 41 metrics.. ([See them](https://github.com/ozlerhakan/AOPMetrics-EkekoAJFX/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj))

### How the metrics work?
---

First of all, make sure that you have all dependencies about the Ekeko Plugin in your Eclipse IDE, if not, you have to download it with its dependencies from [here](https://github.com/cderoove/damp.ekeko/wiki/Getting-Started-with-Ekeko). 

In addition, after importing the Ekeko as an Eclipse project, you have to import the Ekeko's AspectJ extension in order to complete all essential parts. The link of the extension is [here](https://github.com/cderoove/damp.ekeko.aspectj).

Now, you can get the code of the aop.metrics-clj (Ekeko AJFX) by simply cloning the project, plus import it within the workspace of your Eclipse IDE.

