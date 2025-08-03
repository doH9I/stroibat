#!/bin/bash

# Simple build script for Netlify deployment
echo "🚀 Building Construction CRM Frontend..."

# Check if public directory exists
if [ ! -d "public" ]; then
    echo "❌ Error: public directory not found!"
    exit 1
fi

# Check if required files exist
if [ ! -f "public/index.html" ]; then
    echo "❌ Error: index.html not found in public directory!"
    exit 1
fi

if [ ! -f "public/css/style.css" ]; then
    echo "❌ Error: style.css not found in public/css directory!"
    exit 1
fi

if [ ! -f "public/js/app.js" ]; then
    echo "❌ Error: app.js not found in public/js directory!"
    exit 1
fi

echo "✅ All required files found!"
echo "✅ Static site ready for deployment!"
echo "📁 Deploy directory: public/"
echo "🌐 This is a static frontend that connects to a separate API backend"