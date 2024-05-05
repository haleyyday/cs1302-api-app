# Deadline

Modify this file to satisfy a submission requirement related to the project
deadline. Please keep this file organized using Markdown. If you click on
this file in your GitHub repository website, then you will see that the
Markdown is transformed into nice-looking HTML.

## Part 1.1: App Description

> Please provide a friendly description of your app, including
> the primary functions available to users of the app. Be sure to
> describe exactly what APIs you are using and how they are connected
> in a meaningful way.

> **Also, include the GitHub `https` URL to your repository.**

This app combines the Jelly Belly Wiki API and the Color API to allow users
to sort through existing Jelly Beans and generate different color schemes
based on their selection. The program makes calls to the Jelly Belly API
to recieve information about the beans including hex code color, which is
then sent to the Color API to generate palettes.

## Part 1.2: APIs

> For each RESTful JSON API that your app uses (at least two are required),
> include an example URL for a typical request made by your app. If you
> need to include additional notes (e.g., regarding API keys or rate
> limits), then you can do that below the URL/URI. Placeholders for this
> information are provided below. If your app uses more than two RESTful
> JSON APIs, then include them with similar formatting.

### Jelly Belly Wiki API

```
https://jellybellywikiapi.onrender.com/api/beans?groupName=Jelly%20Belly%20Official%20Flavors
```

### The Color API

```
https://www.thecolorapi.com/scheme?hex=0047AB&mode=monochrome&count=4
```


## Part 2: New

> What is something new and/or exciting that you learned from working
> on this project?

I learned a lot about JavaFX front-end formatting functions, like ScrollPanes
and how to use CSS stylesheets and in-line style comments to change formatting
in very specific ways.

## Part 3: Retrospect

> If you could start the project over from scratch, what do
> you think might do differently and why?

If I could redo the project, I would try to maximise the user-friendliness
by making more consistent design choices. I'd also like to add more specific
options on changing the API queries over the limited dropdown options.