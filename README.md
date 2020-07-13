Game Project - README Template
===

# Game
## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Game is an app that allows users to form community around their favorites sports such as Basketball, soccer, and more to engage in conversations and organize games. 

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Sports/ Entertainment 
- **Mobile:** Mobile only 
- **Story:** This is a social platform, where people can organize and participate team sports games. 
- **Market:** People who enjoy team sports. 
- **Habit:** a couple times a week. 
- **Scope:** scroll through the feed of personalized events.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* a user can make an account and login 
* a user can setup a profile pic during signup or after login
* a user can search & join a community
* a user can create an event in a community
* a user can search for an event
* a user can see events in a certain community using various filters (popularity, distance from them, and latest). 
* a user can see a personalized feed of events generated from a probability distribution of the user's.
* a user can see events on his profile that they are indicated as the ones they are going to/communities they are involved in.
* a user can create a community 

**Optional Nice-to-have Stories**

* users can interact with events, comment, like and talk. 
* a user can scroll through a horizontal tab of events with labels of how many new events they have not yet seen. 
* a user can be directed by the map to the location 

### 2. Screen Archetypes

* sign up screen
   *  a user can login/logout of an account
   *  a user can setup a profile pic
* login screen
   *  a user can login/logout of an account.
*  Profile Screen
   * a user can see events on his profile that they are indicated as the ones they are going to/communities they are involved in.
   * a user can setup a profile pic
* Events feed
   * a user can see a personalized feed of events generated from a probability distribution of the user's.
* Detail event
   * users can interact with events, comment, like and talk.
* Community-specific event feed
   *a user can see events in a certain community using various filters (popularity, distance from them, and latest).
* Start an event
   * a user can start a text thread publicly or in a community
* Search Community/event
   * a user can search and join a community
   * a user can search for an event
* Start a community
   * a user can start a community
### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Events
    * Event Feed 
* Search
    * Search Community
    * Search events 
* Profile
    * Profile screen

**Flow Navigation** (Screen to Screen)

* Event Screen
   * event Screen 
   * community-specific screen 
   * Event Detail
* Search
   * community-specific screen 
   * Event Detail screen
* Profile
   * map screen 
   * Event Screen
   * Community-specific Screen

## Wireframes
![](https://i.imgur.com/lPzAzdp.jpg)

## Schema 

### Models
<!-- 1. Post

| Property | Type     | Description |
| -------- | -------- | -------- |
|objectId     | String    | unique id for the post |
|author | Pointer to User | the author of the post|
|image | File	|image that user posts|
|Text |	String |	the content of a post|
|commentsCount|	Number|	number of comments that has been posted to an image|
|community| Pointer to Community| the community this post will be visible in|
|likesCount|	Number|	number of likes for the post|
|createdAt|	DateTime|	date when post is created (default field)|
|updatedAt|	DateTime|	date when post is last updated (default field)| -->
1. User

| Property | Type     | Description |
| -------- | -------- | -------- |
|objectId     | String    | unique id for the user post |
|Username | String	|a screenName |
|Name | String	| actual name of the user|
|creater |	Pointer to User | the person who created the post|
|postsCount|	Number|	number of posts submitted by a user|
|createdAt|	DateTime|	date when user is created (default field)|
|updatedAt|	DateTime|	date when user is last updated (default field)|

2. Community

| Property | Type     | Description |
| -------- | -------- | -------- |
|objectId     | String    | unique id for the user post |
|name | File	|a unique name of the community |
|creater |	Pointer to User | the person who created the post|
|postsCount|	Number|	number of posts in the community|
|createdAt|	DateTime|	date when community is created (default field)|
|updatedAt|	DateTime|	date when community is last updated (default field)|

3. Event

| Property | Type     | Description |
| -------- | -------- | -------- |
|objectId     | String    | unique id for the user post |
|creator |Pointer to User | the user who created the event|
|Title  |	Pointer to User | the user who likes a post|
|Address | String |the address of the Match|
|coordinates | String |Map coordinates|
|createdAt|	DateTime|	date when the user likes a post|


4. Interact (User - event)

| Property | Type     | Description |
| -------- | -------- | -------- |
|objectId     | String    | unique id for the user post |
|text  | String	|content of the comment |
|user |	Pointer to User | the person who created the post|
|event |	Pointer to Event| Comments to a post|
|AttendStatus | Boolean |whether attending or not attending|
|LikeStatus | Boolean | whether likes or not| 
|Comment |String | text of the comment written by the user| 
|createdAt|	DateTime|	date when the first interaction is created (default field)|
|updatedAt|	DateTime|	date when comment is last updated (default) field)|

5. Follow (user - community)

| Property | Type     | Description |
| -------- | -------- | -------- |
|objectId     | String    | unique id for the user post |
|follower |	Pointer to User | the user who follows a community|
|community |Pointer to Community | A community that the user is following|
|EventAttend |Number | number of events in attended from this community|
|Interaction |Number | number of times the user checks, clicks on an event in the community, or create an event in the community|
|createdAt|	DateTime|	date when the user follows a community|

