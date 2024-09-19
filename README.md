# Strong Giraffe

A workout logger that prioritizes entering meaningful data to help you make meaningful training choices.

For example, progressive overload is an important aspect of most training programs. If you want to
increase the difficulty of a workout week to week, you need an accurate measure of your performance
that is consistent over time. However, there are ways logging can go awry. 

Although machines can ostensibly be used for the same movement, one might be more difficult than
other for various reasons. 

Instead of recording sets at different machines as the same workout, or having to create custom 
workouts for every machine. This app aims to let you be as specific as you want in your logging. 

A set of a movement is conceptualized with these pieces:
- Location: These exist to differentiate equipment between different locations.
- Equipment: What are you using to workout? Are you doing a hammer curl with a cable or a dumbbell?
- Exercise: What movement are you performing? These exist separate from the equipment used for them.
- Reps: How many repetitions of a movement did you perform?
- Weight: With what weight did you perform the movement?
- Intensity: How difficult was the set?
  - No Activation, might as well have been moving your arms through the air
  - Easy, didn't feel close to failure at all the whole set
  - Normal, failed near end of set or had very few reps in reserve
  - Early Failure, was a very hard set, eg. you set out to do 12 but you failed at 5
  - Pain, if the movement felt painful at all during the workout

# Contributing

1. Fork the repo
  - Click on the "Fork" button at the top right of this page.
2. Clone your fork
  - Clone your forked repo to your local machine using
```bash
git clone https://github.com/your-username/your-repo.git
```
3. Create a branch
  - Create a new branch for your contribution:
```bash
git checkout -b feature/my-new-feature
```
4. Make your changes
  - Work on your changes locally. Make sure to follow the coding style and conventions already present in the project.
5. Test your changes
  - Run the app and ensure that your changes do not introduce any new issues. Please make sure to add or update unit tests as needed.
6. Commit your changes
  - Commit your changes with a clear and descriptive commit message
```bash
git commit -m "Add a detailed description of your changes"
```
7. Push your changes
  - Push your changes to your forked repo
```bash
git push origin feature/my-new-feature
```
8. Create a Pull Request
- Go to the original repository on GitHub and submit a Pull Request from your forked branch. Provide a detailed description of the changes you've made and reference any relevant issues.

Contribution Guidelines
- Code Style: Follow the existing coding style and conventions used in the project.
- Issues: Feel free to open an issue if you find a bug or want to suggest a feature.
- Testing: Ensure all existing tests pass, and add new tests where applicable.
- Documentation: Update the documentation if your changes require any updates.

Thank you for your contributions!

## Project Structure

This project's stack is 
- [jetpack-compose](https://developer.android.com/compose)
- [room](https://developer.android.com/training/data-storage/room)
- [compose-destinations](https://github.com/raamcosta/compose-destinations)

The project is broken down like so
- repository, manages interactions with the database 
- model, the types used by business logic, distinct from database entities
- destinations, sets up the ViewModels for the main composable components of the app 
- views, pure composable components with no knowledge of the database
- ui/theme, constants for ui themes 
