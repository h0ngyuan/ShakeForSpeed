# ShakeForSpeed (SFS-v1)

ShakeForSpeed is a fun game where players shake their phones as fast as possible to compete for the highest score. This project is built with UniApp and Vue 3, targeting both web and mini-program platforms.

## Project Structure

- `pages/index`: Home page with user information setup and room joining
- `pages/game`: Game page with shake detection and scoring
- `pages/rank`: Ranking page showing player scores
- `api/mock.js`: Mock API for user data and rankings

## Features

1. **User Information Setup**
   - Avatar selection
   - Nickname input
   - Permission handling for user info, sensors, and location

2. **Shake Detection**
   - Accelerometer support with fallback to gyroscope
   - Shake counting algorithm with threshold filtering
   - Visual feedback with shake animations
   - Vibration feedback on shake

3. **Game Flow**
   - 10-second game timer
   - Automatic score submission
   - Redirect to rankings after game completion

4. **Rankings**
   - Display of player rankings
   - User's position in the ranking

## Dependencies

- Vue 3
- UniApp framework
- @dcloudio/uni-app and related packages

## Development

1. Install dependencies:

   ```bash
   npm install
   ```

2. Run development server:

   ```bash
   npm run dev
   ```

3. Build for production:

   ```bash
   npm run build
   ```

## Permissions

The app requires the following permissions:
- `scope.userInfo`: To display user nickname and avatar
- `scope.activityRecognition` or `scope.gyroscope`: To detect phone shaking
- `scope.userLocation`: To provide regional rankings

## Notes

- The project uses mock data for user information and rankings
- Sensor detection automatically falls back from accelerometer to gyroscope
- The game timer is fixed at 10 seconds
- Shake threshold is set to 3.0 for filtering minor movements

## Future Improvements

- Implement real backend API integration
- Add multiplayer room functionality
- Enhance ranking features with time-based rankings
- Add more visual effects and animations
- Implement game difficulty levels