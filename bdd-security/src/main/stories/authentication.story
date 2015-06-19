Authentication

Narrative: 
In order to protect sensitive user data
As an application owner
I want to have a robust authentication system

Meta: @story authentication

Scenario: Passwords should be case sensitive
Meta: @id auth_case @cwe-178-auth
Given a new browser instance
And the default user from: users.table
When the case of the password is changed
And the user logs in
Then the user is not logged in

Scenario: Present the login form itself over an HTTPS connection
Meta: @id auth_login_form_over_ssl @cwe-295-auth @skip
Given a new browser instance
And the browser is configured to use an intercepting proxy
And the proxy logs are cleared
And the login page
And the HTTP request-response containing the login form
Then the protocol should be HTTPS


Scenario: Transmit authentication credentials over HTTPS
Meta: @id auth_https @cwe-319-auth @skip
Given a new browser instance
And the browser is configured to use an intercepting proxy
And the proxy logs are cleared
And the default user logs in with credentials from: users.table
And the HTTP request-response containing the default credentials is selected
Then the protocol should be HTTPS


Scenario: When authentication credentials are sent to the server, it should respond with a 3xx status code.  
Meta: @id auth_return_redirect @cwe-525 @skip
Given a new browser instance
And the browser is configured to use an intercepting proxy
And the proxy logs are cleared
And the default user logs in with credentials from: users.table
And the HTTP request-response containing the default credentials is selected
Then the response status code should start with 3


Scenario: Disable browser auto-completion on the login form
Meta: @id auth_autocomplete_login_form @cwe-525
Given a new browser instance
And the login page
When the login form is inspected
Then it should have the autocomplete attribute set to 'off'

Scenario: Disable browser auto-completion on the password field
Meta: @id auth_autocomplete_password @skip @cwe-525
Given a new browser instance
And the login page
When the password field is inspected
Then it should have the autocomplete attribute set to 'off'


Scenario: Lock the user account out after 4 incorrect authentication attempts
Meta: @id auth_lockout @skip
Given a new browser instance
And the default username from: users.table
And an incorrect password
And the user logs in from a fresh login page 4 times
When the default password is used from: users.table
And the user logs in from a fresh login page
Then the user is not logged in

Scenario: Display a Captcha after 4 failed authentication attempts
Meta: @id auth_login_captcha @skip
Given a new browser instance
And the default username from: users.table
And an incorrect password
And the user logs in from a fresh login page 4 times
When the login page is displayed
Then the CAPTCHA is displayed
