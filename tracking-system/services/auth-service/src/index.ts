import { buildApp } from './app';
import { config } from './config';

async function start() {
  try {
    const app = await buildApp();
    
    await app.listen({
      port: config.port,
      host: config.host
    });
    
    console.log(`Auth service running on ${config.host}:${config.port}`);
  } catch (err) {
    console.error('Error starting server:', err);
    process.exit(1);
  }
}

start();