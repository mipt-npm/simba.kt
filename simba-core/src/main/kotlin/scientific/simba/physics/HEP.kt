package scientific.simba.physics

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D
import org.apache.commons.math3.random.RandomGenerator
import kotlin.math.sqrt
import scientific.simba.math.times

open class HEPParticle(
        val definition: ParticleDefinition,
        override var kineticEnergy: Double,
        override var momentumDirection: Vector3D,
        override var position: Vector3D,
        var properTime : Double
): Particle{
    override val totalEnergy: Double
        get() = kineticEnergy + definition.mass
    override val totalMomentum: Double
        get() = sqrt(totalEnergy*totalEnergy - definition.mass*definition.mass)
    override val momentum: Vector3D
        get() = totalMomentum*momentumDirection

}

interface HEPPhysicalModel{
    val name: String
    fun sampleSecondaries(rnd: RandomGenerator, particle : HEPParticle, element: Element) : List<HEPParticle>
    fun computeCrossSectionPerAtom(energy :Double, element : Element) : Double
}

interface IonisationLoss{
    val material: Material
    val definition: ParticleDefinition
    fun ionizationLoss(rnd: RandomGenerator, kineticEnergy: Double)
}

interface HEPPhysicalProcess {
    val name: String
    fun isApplicable(particlesDefinition: ParticleDefinition): Boolean
}


abstract class LongStepPhysicsProcess(val physicalModels: Set<HEPPhysicalModel>) : HEPPhysicalProcess {

    abstract fun selectModel(particle : HEPParticle, material : Material) : HEPPhysicalModel

    fun computeMicroscopicCrossSection(particle: HEPParticle, material: Material): DoubleArray {
        val model = selectModel(particle, material)
        return material.elements.map { model.computeCrossSectionPerAtom(particle.kineticEnergy, it) }.toDoubleArray()
    }

    fun sampleSecondary(rnd: RandomGenerator,particle: HEPParticle, material: Material, element: Element): List<HEPParticle> {
        return selectModel(particle, material).sampleSecondaries(rnd, particle, element)
    }
}

fun sampleIndx(rnd: RandomGenerator, probability: List<Double>) : Int{
    val norm = probability.sum()
    val normedProbability = probability.map { it /norm }.toMutableList()
    for (i in 1..normedProbability.size - 1) {
        normedProbability[i] = normedProbability[i] + normedProbability[i - 1]
    }
    assert(normedProbability.last() == 1.0)
    return normedProbability.indexOfFirst { it > rnd.nextDouble()}
}

//fun sampleDistance(rnd: RandomGenerator, mfp : Double) : Do


//fun Material.selectElement(rnd: RandomGenerator, sigma: List<Double>): Int {
//    val sum = sigma.sum()
//    val elementProbability = sigma.map { it / sum }.toDoubleArray()
//    for (i in 1..elementProbability.size - 1) {
//        elementProbability[i] = elementProbability[i] + elementProbability[i - 1]
//    }
//    assert(elementProbability.last() == 1.0)
//    return elementProbability.indexOfFirst { it > rnd.nextDouble()}
//}