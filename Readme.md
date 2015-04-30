##Spring AOP Utility for Debouncing method calls ###
this project contains some utilities do debounce method calls by annotating them. based on:

http://stackoverflow.com/questions/4742210/implementing-debounce-in-java


###Maven Config
to use this in your project, add the following repo:

    <repositories>
        <repository>
            <id>cinefms-apitester</id>
            <url>
                https://infra.cinefms.com/nexus/content/repositories/apitester-release/
            </url>
        </repository>
    </repositories>

and add a dependency:

    <dependency>
        <groupId>com.skjlls.aspects</groupId>
        <artifactId>debouncer</artifactId>
        <version>$VERSION</version>
    </dependency>

###Spring Config
then, in the spring context you want to use it in, specify that you're using annotation based Spring AOP AspectJ auto proxies:

    <aop:aspectj-autoproxy />

note that this, by default, will only proxy based on the interfaces your proxy target implements. methods not declared in the interface will be unaccessible. you can force CGLib proxies based on the class by using this instead:

    <aop:aspectj-autoproxy proxy-target-class="true"/>

###Module Config
finally, you will have to specify your aspects

	<bean name="debounceAspect" class="com.skjlls.aspects.debouncer.impl.DebounceAspect">
	</bean>

###Measure
now, you can start annotating your method calls:

#### simple debouncing.
 
    @Debounce
    public void run() {
        [...]
    }
   
#### async debouncing. 
this will make the method call return null.

    @Debounce(async=true)
    public void run() {
        [...]
    }

#### control the delay 
this will debounce with a return value and a maximum delay of 500ms.

    @Debounce(async=false,delay=500)
    public void run() {
        [...]
    }

etc.