import { EventEmitter } from 'events'
import { create } from 'watchr'
import { Service } from 'managed-service-daemon'

EventEmitter.defaultMaxListeners = 0

async function isStopped (svc) {
  if (svc.getState() === 'STOPPED') return

  return await new Promise((resolve) => {
    setTimeout(() => resolve(isStopped(svc)), 1000)
  })
}

async function npmInstall (tgzPath) {
  const runNpmInstall = new Service({
    name: 'NPM Install Build',
    command: 'npm',
    args: tgzPath ? ['install', tgzPath] : ['install'],
    cwd: './Example',
    logFile: {
      path: './npm-install.log',
      printTTL: Infinity
    }
  })

  await runNpmInstall.start()

  return await isStopped(runNpmInstall)
}

async function npmPack () {
  const runNpmPack = new Service({
    name: 'NPM Pack Build',
    command: 'npm',
    args: ['pack']
  })
  const runNpmVersion = new Service({
    name: 'NPM Version Build',
    command: 'npm',
    args: ['version', 'prerelease', '--no-git-tag-version']
  })

  await runNpmVersion.start()
  await isStopped(runNpmVersion)
  await runNpmPack.start()

  return await isStopped(runNpmPack)
}

async function main () {
  const reactNativePath = './node_modules/.bin/react-native'
  const runAndroid = new Service({
    name: 'React Native Android Build',
    command: 'node',
    args: [reactNativePath, 'run-android'],
    cwd: './Example',
    startWait: 300,
    logFile: {
      path: './rn-android.log',
      print: true
    }
  })
  const runServer = new Service({
    name: 'React Native JS Server',
    command: 'node',
    args: [reactNativePath, 'start', '--reset-cache'],
    cwd: './Example',
    startWait: 500,
    logFile: {
      path: './rn-server.log',
      printTTL: Infinity
    }
  })

  await npmInstall()
  await runServer.start()
  await runAndroid.start()

  const path = process.cwd()
  const watcher = create(path)
  let installing = false
  let packing = false

  watcher.watch((error) => {
    if (error) return console.log('watch failed on', path, 'with error', err)
    console.log('watch successful on', path)
    console.log('You may begin making changes to code.')
  })
  watcher.on('change', async (changeType, fullPath) => {
    // Ignore node_modules, Example dir, etc.
    if ((/([\/\\]node_modules[\/\\]|[\/\\]Example[\/\\]|\.log$|scripts[\/\\].*\.mjs$)/gu).test(fullPath)) return

    if (!installing && (changeType === 'create') && (/\.tgz$/gu).test(fullPath)) {
      installing = true
      setTimeout(async () => {
        console.log('Installing local changes with', fullPath)
        await runServer.stop()
        await runAndroid.stop()
        await npmInstall(fullPath)
        await runServer.start()
        await runAndroid.start()
        installing = false
      }, 3000)
    }

    if (!packing && (/(\.js$|\.java$|[\/\\]ios[\/\\])/gu).test(fullPath)) {
      packing = true
      await npmPack()
      packing = false
    }
  })
}

await main().catch(console.error)
