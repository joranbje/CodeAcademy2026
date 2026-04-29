# Dockerfile Contract
# Multi-stage build for Next.js Idempotweet Application
# Location: 1-DevOps/2-Docker/Dockerfile
# Build context: 1-DevOps/idempotweet (set by workflow)

# Stage 1: Install dependencies
FROM node:20-alpine AS deps
RUN apk add --no-cache libc6-compat
WORKDIR /app

# Enable corepack for Yarn v4
RUN corepack enable

# Copy package files (from build context: 1-DevOps/idempotweet/)
COPY package.json yarn.lock .yarnrc.yml ./

# Install dependencies
RUN yarn install --immutable

# Stage 2: Build the application
FROM node:20-alpine AS builder
WORKDIR /app

# Enable corepack for Yarn v4
RUN corepack enable

# Build-time arguments for NEXT_PUBLIC_ variables (baked into the build)
ARG NEXT_PUBLIC_ENABLE_IDEM_FORM=false
ARG NEXT_PUBLIC_SHOW_SEEDED_IDEMS=false
ENV NEXT_PUBLIC_ENABLE_IDEM_FORM=$NEXT_PUBLIC_ENABLE_IDEM_FORM
ENV NEXT_PUBLIC_SHOW_SEEDED_IDEMS=$NEXT_PUBLIC_SHOW_SEEDED_IDEMS

# Copy dependencies from deps stage
COPY --from=deps /app/node_modules ./node_modules
COPY --from=deps /app/.yarn ./.yarn
COPY . .

# Build the Next.js application
RUN yarn build

# Stage 3: Production image
FROM node:20-alpine AS runner
WORKDIR /app

ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1

# Create non-root user for security
RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

# Copy standalone build output
COPY --from=builder /app/public ./public
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

USER nextjs

EXPOSE 3000

ENV PORT=3000
ENV HOSTNAME="0.0.0.0"

CMD ["node", "server.js"]
