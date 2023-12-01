# DownloadAndNotify
Udacity project for implementing custom view animations, downloads and notifications

Project Steps

    Create a radio list of the following options where one of them can be selected for downloading:
        https://github.com/bumptech/glide
        https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter
        https://github.com/square/retrofit
    Create a custom loading button by extending View class and assigning custom attributes to it
    Animate properties of the custom button once it’s clicked
    Add the custom button to the main screen, set on click listener and call download() function with selected Url
    If there is no selected option, display a Toast to let the user know to select one.
    Once the download is complete, send a notification with custom style and design
    Add a button with action to the notification, that opens a detailed screen of a downloaded repository
    Create the details screen and display the name of the repository and status of the download
    Use declarative XML with motionLayout to coordinate animations across the views on the detail screen
    Add a button to the detail screen to return back to the main screen.
