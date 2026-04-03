The backend of mates utilizes Java Spring boot architecture to achieve RESTFUL API. Due to Spring Boot’s architecture, it is best to understand how the program functions by chunking it into each element Spring uses.

Model: This is the data object that each user, profile, swipe, message, etc uses. Some of which such as profile and user utilize SQL relationships such as One to One (one user object to profile) or many to one (many swipes for one user). For when a component of a model is necessary for model creation, @NotNull and @NotBlank are used to ensure that the model component cannot be null or blank. This is used in USER in which every account needs that specific information needed. Each model has a @Getter and @Setter from the lombok class as well as a @NoArgsConstructor and @AllArgsConstructor constructing based on the # of parameters filled out. 

Each model exists a DTO object: These DTO objects exist such that the user isn't touching the model object directly due to security concerns. As such they only contain some of the information from the base model. For example, the user DTO has all components besides the ID. Some DTOs are request -> response DTOs in which we are given a request with specific information and are given a response that may only have some components of the model it is based on. Additionally there may be helper methods inside the DTO where I found issues with using lombok due to IDE errors. All DTOs have the @DATA lombok which bundles getter, setter, toString and other lombok methods together for easier access. However, on my end there appear to be some optimizations to be done which will be discussed further. 
These DTOS interact with the controllers, which serve as the endpoints for frontend communication. Each controller returns a response entity of the type object that it’s responsible for (type profile for profile controller for example). These response entities are also responsible for giving a HTTP response based on if the controller succeeded or failed in its task. Taking one controller for example, User controller:

 @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(currentUser);
    }

Each controller API method uses a specific CRUD mapping, such as “Get” which is used for retrieving information. In this specific API, what is being returned is a responseEntity of USER which by getting the current user, if that user exists return an “OK” HTTP 200 response and the current user information ( return ResponseEntity.ok(currentUser); ), otherwise if there isn't a current user, return a response entity with an UNAUTHORIZED HTTP status 401. Also to note, is that due to using JWT token encryption, each controller has a security check to ensure that someone who is trying to access the endpoint is a specific user. Otherwise, any one would be able to hit the endpoints without an account which would cause a security headache. Additionally, each controller will have a @RestController bean and a @RequestMapping bean with the latter having a basepath “/api/xyz” with xyz being the specific controller endpoint. This path is further extended with each API endpoint having a specific ending path (/me) for the get method above. On the frontend, the specific example above would look like “mates/api/users/me” on the search bar. 

More complicated controllers will call on specific services from the service class in order to facilitate the business logic. You can tell when a file is a service class with the @Service bean. Each service is broken into different methods that all use the same models/dtos needed for a specific controller endpoint. For example, all of the methods in the profiles service are made for the purpose of using the profile model/dto to facilitate actions done in the profile controller. Sometimes, some services will require repositories that deal with other models/dtos. 

Repositories are used for touching the database side of SpringBoot. Although this specific project uses H2 (computer’s internal storage), the repository if connected to an SQL database allows for more enhanced database operations. I declare method signatures within the repository which the service calls which Spring is then able to create SQL queries for.

All of these layers are built on top of a security layer which takes care of all things security such as password encryption, JWT token creation and continuity between who is accessing what on which computer. Admittedly, this is the most vibecoded section of the project in which I know the least about that I had claude build on top of what I had for the rest of the spring project I had coded myself.

All of these layers are run by the Application.java file. 




			### MISC/odds and ends and AI transparency
With how AI has been affecting the workplace for programmers, I wanted a project to reflect the current trend of AI driven development. Thus, I find it adamant that I be as transparent as possible where I used AI in this project and the logic behind my choices.
I had Claude look over my code a few times within the development process for cases to help me understand error codes and help fill in the logic gaps of how I am supposed to do specific things with Springboot (such as security and HTTP responses). I was responsible for figuring out what models, controllers and the overall functionality of what the app did while making sure I had the best understanding of what Claude was programming for me. Because of this, there are some extra methods such as delete methods in some of my controllers that claude made for me  if I wanted to further implement more functionality beyond the api methods I wanted to use. My usage of AI plays a similar role at my job, where I take a more architectural role while copilot helps with the technical steps of building the code. After each segment of code was programmed, I made sure to check what Claude produced, test on Postman and understand what it was trying to accomplish. Due to any agentic AI not being 100% infallible, I had to debug what errors may have occurred. These last steps of testing, debugging, and understanding are the most important parts of the AI development process that separates the vibe coders from developers who work with the agents. It was important to not have blind faith in Claude and to ensure that I still had an active role in the program’s development so that I could own the code I was producing. One example of taking control occurred when I was setting up the backend on render in which I kept getting errors about my getters and setter not being recognized to which I asked Claude what the errors were about, and from that information debugged the code by renaming some of the program files and double checking my imports in the project. 

Future enhancements:
If I had more time to work on this project, I would include more junit test cases for the service class and make sure that code coverage was 100% in both the controller and service classes. Additionally, I would like to refine the MATES concept more to include more specific models for the inclusion of pictures, lease information, account deletion, switching to a MySQL database, and optimization of my imports and remove any possible extraneous code not needed in the project. 






