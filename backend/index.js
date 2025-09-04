const express = require('express');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware to parse JSON bodies
app.use(express.json());

// A simple root route to check if the server is running
app.get('/', (req, res) => {
  res.send('Cat Feeder API is running!');
});

// API routes
const apiRouter = express.Router();
app.use('/api/v1', apiRouter);

// Placeholder for getting current feeding status
apiRouter.get('/status/current', (req, res) => {
  // TODO: Implement actual logic to check Firestore
  res.json({
    status: 'not_fed',
    lastFed: null,
    message: 'The cat has not been fed yet for this mealtime.'
  });
});

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
