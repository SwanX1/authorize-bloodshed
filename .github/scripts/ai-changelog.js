const https = require('https');
const { execSync } = require('child_process');

if (!process.env.OPENAI_API_KEY) {
  console.error("Please provide the OPENAI_API_KEY environment variable");
  process.exit(1);
}

function request(options, data) {
  return new Promise((resolve, reject) => {
    const req = https.request(options, res => {
      let body = '';
      res.on('data', d => {
        body += d;
      });
      res.on('end', () => {
        resolve(JSON.parse(body));
      });
    });

    req.on('error', error => {
      reject(error);
    });

    req.write(JSON.stringify(data));
    req.end();
  });
}

let version_end = execSync('git describe --tags --abbrev=0 --always').toString('utf-8').trim();
let version_start = execSync(`git describe --tags --abbrev=0 ${version_end}~ --always`).toString('utf-8').trim();

if (version_start.length === 40) {
  // No previous tag
  version_start = execSync(`git rev-list --max-parents=0 HEAD`).toString('utf-8').trim();
  version_end = execSync(`git describe --abbrev=0 --always`).toString('utf-8').trim();
}

let GIT_LOG = execSync(`git log --decorate=full ${version_start}..${version_end}`).toString('utf-8');

// Remove last commit (it is for previous tag)

GIT_LOG = GIT_LOG.slice(0, GIT_LOG.lastIndexOf("\ncommit "));

// Get AI generated changelog
request({
  hostname: 'api.openai.com',
  path: '/v1/chat/completions',
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${process.env.OPENAI_API_KEY}`
  }
}, {
  model: "gpt-4",
  messages: [
    {
      "role": "system",
      "content": `You are a changelog writer for a Minecraft mod. You turn Git commit logs into user-friendly changelogs. Exclude internal code changes.`
    },
    {
      "role": "user",
      "content": GIT_LOG
    }
  ],
  temperature: 0,
  max_tokens: 512,
  top_p: 1,
  frequency_penalty: 0,
  presence_penalty: 0,
}).then(response => {
  if (response.error) {
    console.error(response.error);
    process.exit(1);
  }

  process.stdout.write(response.choices[0].message.content);
});