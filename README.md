   Master Thesis
====================

The aim of my master project is: analysing the existing **AspectJ** applications. In order to collect proper informations, I have implemented several **aspect-oriented programming (AOP) Metrics**.


### The Definitions of The Metrics
---

The list shows the implemented metrics along with their abbreviations and definitions :

Abbreviations | Definitions
--- | --- 
 **VS**|	counts the number of classes and aspects.
 **LOC**|	counts the number of lines of code lines without blank lines.
 **NOA**| 	counts the number of fields declared in both aspects and classes.
**NOM**| 	counts the number of methods declared in both aspects and classes.
**NOAd**| 	counts the number of advices.
**NOI**|	counts the number of intertype method declarations.
 **AM**|	counts how many times the methods of classes are called in the body of advices in aspects.
 **IM**|	counts how many times the methods of classes are called in the body of intertype method declarations in aspects.
 **MM**|	counts how many times the methods of classes are called in the body of methods in aspects.
 **AtC**|	counts the number of classes defined as the types of fields in aspects.
 **AC**|	counts the number of classes defined as the types of parameters or return types of advices in aspects.
 **IC**|	counts the number of classes defined as the types of parameters or return types of intertype method declarations in aspects.
 **MC**|	counts the number of classes defined as the types of parameters or return types of methods in aspects.
 **PC**|	counts the number of classes defined as the parameters of pointcut definitions in aspects.
 **PM**|	counts the number of methods and constructors declared in classes are refered by join points defined by pointcuts in aspects.
 **AA**|	counts the number of advance advices whose poincuts have at least one of the advance pointcuts such as if, adviceexecution, cflow, and cflowbelow.
 **BA**|	counts the number of basic advices whose pointcuts use more than once the basic pointcuts.
 **InA**|	counts how many aspects inherited by abstract aspects.
 **SA**|	counts how many aspects are isSingleton.
 **nSA**|	counts how many aspects are non-isSingleton.
 **AE**|	counts how many times adviceExecution is used in the body of advices in aspects.
 **AJPS**|	calculates the average of the join point shadows per advice.
**AgPA**|	calculates the average of size of arguments declared in args per advice.
**AgPAg**|	calculates the average of size of arguments declared in args per advice that has an args.
 **nPC**|	counts how many around advice do not use a proceed call.
 **ArA**|	counts the number of the around advices in a given system.
 **ABA**|	counts the number of the before and after advices in a given system.
 **ATRA**|	counts the number of the after throwing and after returning advices in a given system.
 **AdC**|	calculates the average of advised classes have at least one join point shadow in a system.
 **nAdC**|	calculates the average of non-advised classes have no any join point shadows in a system.
 **NOC**|	counts how many times call pointcut is declared.
 **NOE**|	counts how many times execution pointcut is declared.
 **AdM**|	counts the number of advised methods of classes.
 **nAdM**|	counts the number of non-advised methods of classes.
 **CsC**|	counts how many classes are advised along with their subclasses.
 **ACsC**|	calculates the average of subclasses of advised classes per adviced class.
 **NOW**|	counts the amount of used wildcards in modules named in the pointcuts in given aspects.
 **NOnW**|	counts the amount of non-used wildcards in modules named in the pointcuts in given aspects.
 **TJPS**|	counts the amount of used thisJoinPoint and thisJoinPointStatic in the body of advices.
**MoA**|	counts how many times args are modified in the body of advices.
**AcA**|	counts how many times args are accessed in the body of advices.

### Quick Access
---

The implemented 41 metrics.. ([See them](https://github.com/ozlerhakan/AOPMetrics-EkekoAJFX/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj))

### How to run the metrics?
---

First of all, make sure that you have all dependencies about the Ekeko Plugin in your Eclipse IDE, if not, you have to download it with its dependencies from [here](https://github.com/cderoove/damp.ekeko/blob/master/EkekoPlugin/README.md). 

In addition, after importing the Ekeko as an Eclipse project, you have to import the Ekeko's AspectJ extension in order to complete all essential parts. The link of the extension is [here](https://github.com/cderoove/damp.ekeko.aspectj).

Now, you can get the code of the aop.metrics-clj (Ekeko AJFX) by simply cloning the project, plus import it within the workspace of your Eclipse IDE.

