# ICE Server/Client

---
#### Note that this software is still in pre-alpha! Which means it may be unstable and may be deployed with bugs.
---

> # Table Of Contents:
> 
> * [What is ICE?](#what-is-ice)
> * [Architecture And Design](#architecture-and-design)
>     * [Services](#services)
>     * [IOC](#ioc-inversion-of-control)
>     * [Communication](#communication)
>     * [ICE Protocol](#ice-protocol)
>     * [Message's Type Explenation](#messages-type-explenation)
>     * [LEN Attribute Explenation](#len-attribute-explenation)
>     * [Server Responses List](#server-responses-list)
>     * [Principles And Concepts ICE following Fully Or Partly](#principles-and-concepts-ice-following-fully-or-partly)
>     * [Principles](#principles)
>     * [Design Patterns](#design-patterns)
>     * [Concepts](#concepts)
> * [Features](#features)
> * [Technologies](#technologies)
> * [How To RUN](#how-to-run)
> * [Screenshots](#screenshots)

# What is ICE?

ICE is a real-time messaging software that allows you to connect to a single server and exchange messages with other participants connected to the same server.

# Architecture And Design

ICE has a ***monolithic*** architecture combined with **event-driven paradigm** in order to overcome the drawbacks of a monolithic application.

### Services

ICE is powered by **completely independent loosely-coupled internal microservices** (**Not to be confused with microservice architecture**) that communicate with each other using **events**. Thanks to the fact that we avoid relying on dependencies and instead rely on events, we get significant benefits such as, **maintainability, scalability, and testability**. The only major dependencies almost every service requires are `EventBus` and `DataAccess` objects.

The thing that enables the total independence of services that are contained within the same monolithic software is events. Instead of injecting dependencies to dependents, each service lives independently and communicates with other services by sending events to them. Sure, an event sent by one service must be known by the other service in order to be handled, but just from looking you can see that the code is much more maintainable and scaleable this way. Look at the differences between the both approaches - dependency injection vs event-driven:

* ***Events approch:***
    
    ![Event-Driven drawio](https://user-images.githubusercontent.com/62857161/147893335-ddae72f4-bb38-48af-9ab4-607659d52105.png)

* ***Dependency injection approch:***
    
    ![Dependency-Injection drawio](https://user-images.githubusercontent.com/62857161/147893463-74dd379a-3edf-462a-94c9-94cfbe1584b2.png)

You can see that we have a lot of circular dependencies here, but no one really develops software this way. The components are not going to communicate directly to each other but through some kind of mediator. So of course, we can use design patterns to solve some of the issues with the dependency injection, but still, the code is much more clearer and maintainable using events. In this way, dependency injection is used only to inject the EventBus and other essential dependencies, but not services into other services. 

The **event system** that ICE uses was written by me and it implements the **Publish-Subscribe** design pattern - [ICE's Events Framework](https://github.com/RonBY14/IEF)

### IOC (Inversion Of Control)

All of the system's singletons are managed and created by the Spring container. The `EventBus` and the `DataAccess` objects are injectd also by spring container.

## Communication

ICE uses plain TCP socket (`java.net.Socket`) and a custom application-layer protocol (Named ICE protocol) for communication with the server.

### ICE Protocol

Is a custom protocol I designed with no use of a known existing protocol (Like, **XMPP** or **HTTP**). It includes a number of rules and constraints:

 **Note:** when referring to the term "message" here, it means request/response.

- Must be in XML format without prolog.
- Must be encoded in UTF-8.
- Each message must include header, which is represented by the root tag. The header must include 3 elements:
    - Message type - is determined by the name of the root tag and must be only 5 bytes long.
    - Attribute `id` - whose value must be numeric only 5 bytes long. ***This attribute's functionality is not yet implemented but must be present in the header!***.
    - Attribute `len` - whose value must be numeric only 5 bytes long.
- The message type (root) and body (root internals), must be supported by both ends in order to have effects.

Each message is represented by a Java class for convenience and each the server and the client must own the request class and the associated response class.

### Message's Type Explenation

Each message must be known by both ends in order to be successfully processed. The type of the message indicates its purpose and what actions should be taken according to the its type. If the message is supported by the receiving end, the relevant actions will be performed. As the rules says, the message's type is only 5 bytes long, so, custom acronyms are used to represent it (For example: `authe` = authentication, `badrq` = bad request).

### LEN Attribute Explenation

Because this connection is a persistent connection, messages may be sent from the other end one after the other and it will not be possible to know where one message ends and the other begins. The solution I took for this problem is that the sender end who generates the message must on his side calculate the number of bytes that make up the message and embed this length value into the `len` attribute located in the message's header.

### Server Responses List:

* `badrq` (BadRequest) - A response to an `invalid` or `unsupported` request sent by a client to the server. 
* `messg` (Message) - A response to a delivery (`delvr`) request sent by a client so the server can route this message to the desired recipient/s. This response is sent to the recipient/s.
* `align` (Alignment) - A response that may be **broadcasted (per-connection)** or **unicasted (per-request)**. This response is sent to all clients in case a new client connects to the server or there is a change in the "visible data" inside the server. This response includes a list of all current chat participants.
* `authe` (Authentication) - Response to an authentication request sent by client that want to connect.

## Summary

ICE is a monolithic event-driven software, powered by independent loosely-coupled internal microservices created by spring, and communicate with a server using TCP socket and a custom application layer protocol named ICE protocol.

## Principles And Concepts ICE Following Fully Or Partly

### Principles

* SOLID. 
* DRY.
* YAGNI.

### Design Patterns

* Pub-sub.
* Singleton.
* Dependency injection.
* Factory. 
* Callback.

### Concepts

* Object-oriented programming.
* Object-oriented design.
* Event-driven development.

# Features

* Connect to a server with a specific IP.
* Disconnect from a server.
* Choosing nickname.
* Switching from dark and light theme.
* Local chat background.
* Inspect network status.
* Participants list view.
* Conversation view.
* Text bubbles.
* helvetica font, which supports Hebrew and basic emojis.
* Sending messages.
* Receiving messages.
* Deleting conversation for you.
* Real-time in chat view of new connections/disconnections in the server.

# Technologies

- Java.
- XML.
- Maven - to manage and import project dependencies.
- Spring - used for bean management, creation and injection.
- Swing - swing is used with FlatLAF and MigLayout plugins for the UI.
- IEF (ICE's Events Framework) - framework I designed - [IEF](https://github.com/RonBY14/IEF).

# How To RUN

> ### Keep in mind that your messages are not encrypted!

- The only requirement is that you must have **Java 11 or above**.

1. In order to start chatting you have to first download the server and the client's executables from the following links:

    * [Server Software](url)
    * [Client Software](url)  

2. Start by running the server, which by default will run on port `3348`.
3. Now you can start one or more instances of the client program.
4. Inside your client instance, fill the relevant fields with the remote/loopback address, and your nickname.

# Screenshots

All screenshots: 

## Alpha Screenshoot

![Darktheme](https://user-images.githubusercontent.com/62857161/147793358-ca6b3d54-be5e-4dad-911b-65aab87fc490.png)

## Prototype Screenshot

![image](https://user-images.githubusercontent.com/62857161/145385804-464e4a5f-f5d1-4849-8320-3aba7e201f2b.png)
