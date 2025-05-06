import * as THREE from 'https://esm.sh/three@0.160.0';
import { OrbitControls } from 'https://esm.sh/three@0.160.0/examples/jsm/controls/OrbitControls';

const scene = new THREE.Scene();

const camera = new THREE.PerspectiveCamera(
    75, window.innerWidth / window.innerHeight, 0.1, 1000
);
camera.position.z = 5;

const renderer = new THREE.WebGLRenderer();
renderer.setSize(window.innerWidth, window.innerHeight);
document.body.appendChild(renderer.domElement);
// renderer.domElement is the HTML <canvas> element created by Three.js—it's what shows the visuals. You append it to the page so it's visible.

const controls = new OrbitControls(camera, renderer.domElement);
controls.enableDamping = true; // adds smoothness to motion
controls.dampingFactor = 0.05;


const sunGeometry = new THREE.SphereGeometry(1,32,32);
const sunMaterial = new THREE.MeshBasicMaterial({ color: 0xffff00 });
const sun = new THREE.Mesh(sunGeometry, sunMaterial);
scene.add(sun);

function createPlanet(radius, color, orbitRadius, orbitSpeed) {
    const planetGeo = new THREE.SphereGeometry(radius, 32, 32);
    const planetMat = new THREE.MeshBasicMaterial({ color });
    const planet = new THREE.Mesh(planetGeo, planetMat);
    planet.position.x = orbitRadius;
  
    const orbitGroup = new THREE.Object3D();
    orbitGroup.userData = { speed: orbitSpeed };
    orbitGroup.add(planet);
    scene.add(orbitGroup);
  
return orbitGroup;
}

const orbits = [];

/// Normalize orbit speed: speed = (1 / period) * scaleFactor
const speedFactor = 0.005;

const mercuryOrbit = createPlanet(0.2, 0xaaaaaa, 2.0, 4.17 * speedFactor);
const venusOrbit   = createPlanet(0.25, 0xffcc99, 2.7, 1.61 * speedFactor);
const earthOrbit   = createPlanet(0.3, 0x0000ff, 3.5, 1.00 * speedFactor);
const marsOrbit    = createPlanet(0.28, 0xff4500, 4.2, 0.53 * speedFactor);
const jupiterOrbit = createPlanet(0.6, 0xd2b48c, 5.5, 0.084 * speedFactor);
const saturnOrbit  = createPlanet(0.5, 0xffd27f, 7.0, 0.034 * speedFactor);
const uranusOrbit  = createPlanet(0.4, 0x66ffff, 8.2, 0.012 * speedFactor);
const neptuneOrbit = createPlanet(0.4, 0x3333ff, 9.5, 0.0061 * speedFactor);

const ringGeo = new THREE.RingGeometry(0.6, 0.9, 64);
const ringMat = new THREE.MeshBasicMaterial({ color: 0xd2b48c, side: THREE.DoubleSide });
const ring = new THREE.Mesh(ringGeo, ringMat);
ring.rotation.x = Math.PI / 2;
saturnOrbit.children[0].add(ring);

orbits.push(mercuryOrbit, venusOrbit, earthOrbit, marsOrbit, jupiterOrbit, saturnOrbit, uranusOrbit, neptuneOrbit);

function addStarField(count = 1000) {
    const geometry = new THREE.BufferGeometry();
    const positions = [];

    for (let i = 0; i < count; i++) {
        const x = (Math.random() - 0.5) * 200;
        const y = (Math.random() - 0.5) * 200;
        const z = (Math.random() - 0.5) * 200;
        positions.push(x, y, z);
    }

    geometry.setAttribute(
        'position',
        new THREE.Float32BufferAttribute(positions, 3)
    );

    const material = new THREE.PointsMaterial({ color: 0xffffff, size: 0.5 });
    const stars = new THREE.Points(geometry, material);
    scene.add(stars);
}

    addStarField();
function fadeInAudio(audio, targetVolume = 0.5, duration = 3000) {
  const steps = 60;
  const interval = duration / steps;
  let currentStep = 0;

  const fade = setInterval(() => {
    currentStep++;
    const progress = currentStep / steps;
    audio.setVolume(progress * targetVolume);

    if (currentStep >= steps) {
      clearInterval(fade);
      audio.setVolume(targetVolume); // ensure exact final value
    }
  }, interval);
}

const listener = new THREE.AudioListener();
camera.add(listener);

const sound = new THREE.Audio(listener);

const audioLoader = new THREE.AudioLoader();
audioLoader.load('rihanna_trim.mp3', function (buffer) {
  sound.setBuffer(buffer);
  sound.setLoop(true);
  sound.setVolume(0); // start silent
  sound.play();

  fadeInAudio(sound, 0.5, 3000); // targetVolume, duration(ms)
});


function animate() {
    requestAnimationFrame(animate);
    sun.rotation.y += 0.01;
    // Rotate each orbit group
    for (const orbit of orbits) {
        orbit.rotation.y += orbit.userData.speed;
    }

    controls.update();

    renderer.render(scene, camera);
}

animate();
