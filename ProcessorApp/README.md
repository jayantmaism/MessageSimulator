Created By Jayant Vyas on 21/02/2020

How to run the program:
Start DataProcessorApp as a java application then It will start application flow. 
It is available in com.data.test.processor package.



Package details:
com.data.test.processor
com.data.test.processor.common
com.data.test.processor.customexception
com.data.test.processor.dto
com.data.test.processor.message


Class Details:
com.data.test.processor
DataProcessorApp.java :: Starting point of the execution
MessageProcessor.java  :: It is heart of the application, It is creating all the consumer and producers with threadpool.


com.data.test.processor.common :
This package contains all the common things which we are using.
MessageProcessorConstants.java :: Its have all the constants which we are using in this App
MessageProcessorUtility.java :: Utility have a common methods which we can reusein our app.


com.data.test.processor.customexception
InvalidSourceException.java :: If we get the message form other than source A , B , C and D then We will throw our custom exception.
If business case will be increase and our code base will be increase then We need our custom exception to throw business related exception which we know.

com.data.test.processor.dto
FinalResultSetABCD.java :: Its a interface and It have all the common functionality which we will implement in our classes.
SourceABImpl.java ::  It's have the message value and description for source A and B
SourceCDImpl.java ::  It's have the message value and description for source C and D


com.data.test.processor.message
MessageProducer.java :: It will produce the messages for respective source which we have defined.
Dispatcher.java   ::    It will identified the message source and put into respective message source queue
ConsumerTask.java ::    It will take the messages from queue and add into ResultSetQueue.


 
