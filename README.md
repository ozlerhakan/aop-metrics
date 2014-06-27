### Master Thesis  - Measuring Aspect-Oriented Software In Practice
---

Aspect-oriented programming (AOP) is a way of modularizing a software system by means of new kind of modules called aspects in software development. To this end AOP helps in alleviating crosscutting concerns of system modules by separating into several aspect modules, thereby aiming to improve separation of concerns. On the other hand, aspects can bring unexpected behaviour to a system while attempting to alter the system’s concerns. They can modify the behaviour of the base system without warning. Following to this, such impact can limit to achieve modular reasoning in an aspect-oriented system properly.

Obtaining the valuable data, we try to get an idea of how difficult it is to achieve modular reasoning. In this thesis, we analyse the existing ten **[AspectJ](http://eclipse.org/aspectj/)** systems by answering six research questions. These six questions were derived from our general question: *"how AspectJ is used in practice?"*. In order to answer each one of them, we have implemented
a metrics suite including both **[aspect-oriented ](http://en.wikipedia.org/wiki/Aspect-oriented_programming)** and **[object-oriented ](http://en.wikipedia.org/wiki/Object-oriented_programming)** features using Ekeko. Next to modular reasoning, we also acquire other usefulness about AOP constructs and coupling between classes and aspects. These results can then be used to influence the design of existing or new AOP languages, or to improve existing analysis tools.

### Hierarchy of The Research Questions 
---

1.	How large is the system?
	*	*Lines of Code* [(LOC)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L156)
	*	*Vocabulary Size* [(VS)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L191) 
	*	*Number of Attributes* [(NOA)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L201) 
	*	*Number of Methods* [(NOM)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L211)
2.	How often are AOP constructs used compared to OOP features?
	*	*Number of Intertypes* [(NOI)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L238)
	*	*Number of Advice* [(NOAd)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L256)
3.	Which AOP constructs are typically used?
	*	*Inherited Aspects* [(InA)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L573)
	*	*Singleton Aspects* [(SA)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L580)
	*	*Non-Singleton Aspects* [(nSA)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L589)
	*	*Advice-Advanced Pointcut Dependence* [(AAP)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L557)
	*	*Advice-Basic Pointcut Dependence* [(ABP)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L567)
	*	*Number of Around Advice* [(NOAr)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L733)
	*	*Number of Before/After Advice* [(NOBA)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L752)
	*	*Number of After Throwing/Returning Advice* [(NOTR)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L770)
	*	*Number of Call* [(NOC)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L893)
	*	*Number of Execution* [(NOE)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L900)
	*	*Adviceexecution-Advice Dependence* [(AE)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L596)
	*	*Number of Wildcards* [(NOW)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L1128)
	*	*Number of non-Wildcards* [(NOnW)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L1140)
	*	*Argument size of Args-Advice* [(AAd)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L722)
	*	*Argument size of Args-Advice-args* [(AAda)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L727)
4.	How many types and members of a system are advised by AOP?
	*	*Percentage of Advised Classes* [(AdC)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L851)
	*	*Percentage of non-Advised Classes* [(nAdC)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L922)
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
	*	*Attribute-Class Dependence* [(AtC)](https://github.com/ozlerhakan/aop-metrics-ekeko/blob/master/Ekeko%20AJFX/src/ekeko_ajfx/AOPMetrics.clj#L366)
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
### Selected Aspect-Oriented Systems
---

1. [HealthWatcher](http://www.kevinjhoffman.com/tosem2012/)
2. HyperCast
3. [AJHotDraw](http://ajhotdraw.sourceforge.net/)
4. [AJHSQLDB](http://sourceforge.net/projects/ajhsqldb/)
5. [Contract4J5](https://github.com/deanwampler/Contract4J5)
6. [MobileMedia](http://sourceforge.net/projects/mobilemedia/)
7. [iBatis](sourceforge.net/projects/ibatislancaster/)
8. [Telestrada](http://www.kevinjhoffman.com/tosem2012/)
9. SpaceWar
	* Comes bundled with the [AJDT](http://eclipse.org/ajdt/)
10. [TetrisAJ](http://www.guzzzt.com/coding/aspecttetris.shtml)


### How the metrics work
---

First of all, make sure that you have all the dependencies about the *Ekeko plug-in* in your [Eclipse Kepler 4.3 IDE](http://www.eclipse.org/kepler), if not, you first need to download the dependencies:

  * AST View [(i.e. org.eclipse.jdt.astview)](http://www.eclipse.org/jdt/ui/astview/index.php)
  * [Counterclockwise](http://www.eclipse.org/jdt/ui/astview/index.php)

Downloading the dependencies, you are now ready to install the prebuilt *Ekeko* plug-in:

  * Go to: ```Help > Install New Software...``` in your Eclipse IDE.
  * Copy and paste this url: http://soft.vub.ac.be/~cderoove/eclipse/ in the Work with text field.
  * Hit Enter.
  * Select all plug-ins including [Ekeko](https://github.com/cderoove/damp.ekeko) and [GASR](https://github.com/cderoove/damp.ekeko.aspectj) and the rest and install all of them.
  * After installing both Ekeko and the Ekeko's AspectJ estension, you are ready to downdload the metrics.
  * Import the Ekeko AJFX project (i.e. the metrics) in your Eclipse workspace.
  * Select an AspectJ project that you want to analyse then, right-click on the project, apply these steps: ```Configure > Include in Ekeko Queries```
  * Some metrics also need soot analysis in order to run properly. To do that, we need to configure the selected AspectJ project once as follows:
      *  Right-click on the project : ```Properties > Ekeko properties```.
      *  Click the Select button and now choose the class that contains the main() method of the AspectJ project. (e.g. you can find an example of a main() method in our AJTestMetrics project ([MainTST](https://github.com/ozlerhakan/aop-metrics/blob/master/AJTestMetrics/src/ua/thesis/test/MainTST.java)).
      *  Write the following line into the "Soot arguments:" one-line text box: ```-no-bodies-for-excluded -src-prec c -f jimple -keep-line-number -app -w -p cg.cha```
      * Click OK
      * Finally, ```right-click the project > Configure > Enable Ekeko Soot Analyses```.

  * Activate an Ekeko-hosted REPL by doing ```Ekeko > Start nRepl``` from the main Eclipse menu. A dialog shows the port on which the nRepl server listens (e.g. ```nrepl://localhost:51721```)
  * Connect to the Ekeko-hosted REPL: Go to: ```Window > Connect to REPL``` to connect to this port (i.e. ```nrepl://localhost:51721```). A Counterclockwise REPL view now opens.
  * Open the ```metrics-result``` file located in the imported project and right-click somewhere on the file and choose ```Clojure > Load file in REPL ```
  * You will see ```nil``` in the REPL which means that everything goes correctly and the metric framework has likewise been loaded in the REPL.
  * After disabling the comment block, you can now run the metrics. For example, if we look at ```(metrics/VSize)```, the alias name of our ```AOPMetrics``` is ```metrics``` that helps in reaching the implemented metrics in a short way rather than typing the totally qualified name (i.e. ```AOPMetrics```). ```metrics/Vsize``` simply retrieves the vocabulary size of the project.



:pushpin:**Note:** The AcA and MoA metrics need different soot arguments to obtain the exact data. Thus, you need to change the current arguments with the following one: ```-no-bodies-for-excluded -src-prec c -f jimple -keep-line-number -app -w -p jb use-original-names:true -p cg.cha``` and run again ```Ekeko Soot Analyses```.


>:exclamation:**One Potential Issue**: There was an encountered issue about soot analysis. If you get the same problem called ```RuntimeException : tried to get nonexistent method```, while attempting to run the metrics especially for the AM, IM, and MM metrics.  You can find more information on it from https://github.com/ozlerhakan/aop-metrics/issues/1 

### License 

Copyright © 2014 Hakan Özler.
